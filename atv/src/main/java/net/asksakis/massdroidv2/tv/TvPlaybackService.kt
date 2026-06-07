package net.asksakis.massdroidv2.tv

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import net.asksakis.massdroidv2.data.sendspin.SendspinManager
import net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient
import net.asksakis.massdroidv2.domain.repository.PlayerRepository
import net.asksakis.massdroidv2.domain.repository.SettingsRepository
import net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher
import net.asksakis.massdroidv2.playback.SendspinCoordinator
import javax.inject.Inject

/**
 * Foreground service that makes the Shield itself a Sendspin player: it drives
 * the shared :core [SendspinCoordinator] (which owns the Sendspin engine + Oboe
 * output) WITHOUT the phone-only Android Auto / Follow Me wiring. With
 * sendspinEnabled defaulting true, the device registers with MA and streams
 * synced audio to the TV/AVR once the WS connects. Tune the AVR's HDMI/ARC
 * offset via the player's static_delay (spec mechanism).
 */
@AndroidEntryPoint
class TvPlaybackService : Service() {

    @Inject lateinit var sendspinManager: SendspinManager
    @Inject lateinit var sendspinVolumeCoordinator: SendspinVolumeCoordinator
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var playerRepository: PlayerRepository
    @Inject lateinit var wsClient: MaWebSocketClient
    @Inject lateinit var shortcutDispatcher: ShortcutActionDispatcher

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var coordinator: SendspinCoordinator

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForegroundCompat()
        coordinator = SendspinCoordinator(
            context = this,
            scope = scope,
            sendspinManager = sendspinManager,
            settingsRepository = settingsRepository,
            playerRepository = playerRepository,
            wsClient = wsClient,
            volumeCoordinator = sendspinVolumeCoordinator,
            shortcutDispatcher = shortcutDispatcher,
            clientName = "MassDroid TV",
            onConnectionStateChanged = {},
            onTargetChanged = {},
            onActive = {},
            onInactive = {},
            onWifiConnected = {},
        )
        coordinator.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        coordinator.destroy()
        scope.cancel()
        super.onDestroy()
    }

    private fun startForegroundCompat() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("MassDroid TV")
            .setContentText("Ready as a synced speaker")
            .setOngoing(true)
            .setSilent(true)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply { setShowBadge(false) }
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "tv_playback"
        private const val NOTIFICATION_ID = 1

        fun start(context: android.content.Context) {
            val intent = Intent(context, TvPlaybackService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
