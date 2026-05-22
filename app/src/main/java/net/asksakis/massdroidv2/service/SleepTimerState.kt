package net.asksakis.massdroidv2.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared sleep timer state holder. The actual timer runs in PlaybackService,
 * but ViewModels observe state and trigger start/cancel through this bridge.
 *
 * Sleep is per-player: a sleep timer started from the Now Playing screen
 * targets *that* player and only fades / pauses it on completion. Other
 * speakers in the system are left alone. The target's [playerId] is part
 * of the active state so UI surfaces (e.g. the Players screen) can show
 * a sleep badge next to the right card.
 */
@Singleton
class SleepTimerBridge @Inject constructor() {

    sealed interface State {
        data object Idle : State
        data class Running(val endTimeMs: Long, val playerId: String) : State
        data class FadingOut(val endTimeMs: Long, val playerId: String) : State
    }

    data class StartRequest(
        val minutes: Int,
        val playerId: String,
    )

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    // Commands from UI to Service
    private val _startCommand = MutableStateFlow<StartRequest?>(null)
    val startCommand: StateFlow<StartRequest?> = _startCommand.asStateFlow()

    private val _cancelCommand = MutableStateFlow(0L)
    val cancelCommand: StateFlow<Long> = _cancelCommand.asStateFlow()

    fun requestStart(minutes: Int, playerId: String) {
        _startCommand.value = StartRequest(minutes, playerId)
    }

    fun requestCancel() {
        _cancelCommand.value = System.currentTimeMillis()
    }

    fun consumeStartCommand() {
        _startCommand.value = null
    }

    fun updateState(state: State) {
        _state.value = state
    }

    fun remainingMs(): Long {
        return when (val s = _state.value) {
            is State.Running -> (s.endTimeMs - System.currentTimeMillis()).coerceAtLeast(0)
            is State.FadingOut -> (s.endTimeMs - System.currentTimeMillis()).coerceAtLeast(0)
            State.Idle -> 0
        }
    }

    /** Convenience: the player the active sleep timer is targeting, if any. */
    fun targetPlayerId(): String? = when (val s = _state.value) {
        is State.Running -> s.playerId
        is State.FadingOut -> s.playerId
        State.Idle -> null
    }
}
