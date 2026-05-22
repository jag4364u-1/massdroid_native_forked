package net.asksakis.massdroidv2.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.asksakis.massdroidv2.R

class SleepTimerManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val bridge: SleepTimerBridge,
    private val onFadeFraction: (Float, String) -> Unit,
    private val onStop: suspend (String) -> Unit,
) {
    companion object {
        private const val TAG = "SleepTimer"
        private const val CHANNEL_ID = "sleep_timer"
        private const val NOTIFICATION_ID = 9002
        private const val FADE_DURATION_MS = 30_000L
        private const val NOTIFICATION_UPDATE_MS = 30_000L
        const val ACTION_CANCEL = "net.asksakis.massdroidv2.SLEEP_TIMER_CANCEL"

        val PRESETS_MINUTES = listOf(15, 30, 45, 60, 90, 120)
    }

    sealed interface State {
        data object Idle : State
        data class Running(val endTimeMs: Long, val playerId: String) : State
        data class FadingOut(val endTimeMs: Long, val playerId: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var commandJob: Job? = null
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    init {
        createNotificationChannel()
        observeCommands()
    }

    private fun observeCommands() {
        commandJob = scope.launch {
            launch {
                bridge.startCommand.collect { request ->
                    if (request != null) {
                        bridge.consumeStartCommand()
                        start(request.minutes, request.playerId)
                    }
                }
            }
            launch {
                var lastCancel = 0L
                bridge.cancelCommand.collect { ts ->
                    if (ts > lastCancel && isRunning()) {
                        lastCancel = ts
                        cancel()
                    }
                }
            }
        }
    }

    private fun setState(state: State) {
        _state.value = state
        bridge.updateState(when (state) {
            State.Idle -> SleepTimerBridge.State.Idle
            is State.Running -> SleepTimerBridge.State.Running(state.endTimeMs, state.playerId)
            is State.FadingOut -> SleepTimerBridge.State.FadingOut(state.endTimeMs, state.playerId)
        })
    }

    fun start(durationMinutes: Int, playerId: String) {
        cancel()
        val durationMs = durationMinutes * 60_000L
        val endTime = System.currentTimeMillis() + durationMs
        setState(State.Running(endTime, playerId))
        Log.d(TAG, "Started: ${durationMinutes}min for $playerId, fade at last ${FADE_DURATION_MS / 1000}s")
        updateNotification()

        timerJob = scope.launch {
            // Main countdown: wait until fade start
            while (isActive) {
                val remaining = endTime - System.currentTimeMillis()
                if (remaining <= FADE_DURATION_MS) break
                val nextUpdate = NOTIFICATION_UPDATE_MS.coerceAtMost(remaining - FADE_DURATION_MS)
                delay(nextUpdate)
                updateNotification()
            }

            if (!isActive) return@launch

            // Fade out over 30 seconds
            Log.d(TAG, "Fade out starting for $playerId")
            setState(State.FadingOut(endTime, playerId))
            updateNotification()
            val fadeStart = System.currentTimeMillis()
            while (isActive) {
                val elapsed = System.currentTimeMillis() - fadeStart
                if (elapsed >= FADE_DURATION_MS) break
                val fraction = 1f - (elapsed.toFloat() / FADE_DURATION_MS)
                onFadeFraction(fraction.coerceIn(0f, 1f), playerId)
                delay(500)
            }

            if (!isActive) return@launch

            // Stop playback on the target player only.
            Log.d(TAG, "Timer expired, stopping playback for $playerId")
            onFadeFraction(0f, playerId)
            delay(500)
            onStop(playerId)
            delay(2000) // wait for playback to fully stop before restoring volume
            onFadeFraction(1f, playerId) // restore original volume
            setState(State.Idle)
            cancelNotification()
        }
    }

    fun cancel() {
        val current = _state.value
        if (timerJob?.isActive == true) {
            Log.d(TAG, "Cancelled")
            timerJob?.cancel()
            val pid = when (current) {
                is State.FadingOut -> current.playerId
                else -> null
            }
            if (pid != null) {
                onFadeFraction(1f, pid) // restore original volume
            }
        }
        timerJob = null
        setState(State.Idle)
        cancelNotification()
    }

    fun remainingMs(): Long {
        return when (val s = _state.value) {
            is State.Running -> (s.endTimeMs - System.currentTimeMillis()).coerceAtLeast(0)
            is State.FadingOut -> (s.endTimeMs - System.currentTimeMillis()).coerceAtLeast(0)
            State.Idle -> 0
        }
    }

    fun isRunning(): Boolean = _state.value !is State.Idle

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sleep Timer",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Sleep timer countdown"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification() {
        val remaining = remainingMs()
        if (remaining <= 0) { cancelNotification(); return }

        val minutes = (remaining / 60_000).toInt()
        val isFading = _state.value is State.FadingOut
        val endTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(System.currentTimeMillis() + remaining))
        val countdown = when {
            minutes >= 60 -> "${minutes / 60}h ${minutes % 60} min"
            minutes > 0 -> "$minutes min"
            else -> "${(remaining / 1000).toInt()}s"
        }
        val text = if (isFading) "Fading out..." else "Stops at $endTime ($countdown)"

        val cancelIntent = Intent(ACTION_CANCEL).apply {
            setPackage(context.packageName)
        }
        val cancelPending = PendingIntent.getBroadcast(
            context, 0, cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val openPending = openIntent?.let {
            PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Sleep Timer")
            .setContentText(text)
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(openPending)
            .addAction(R.drawable.ic_notification, "Cancel", cancelPending)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
