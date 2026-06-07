package net.asksakis.massdroidv2.tv.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.asksakis.massdroidv2.domain.model.Player
import net.asksakis.massdroidv2.domain.model.QueueState
import net.asksakis.massdroidv2.domain.model.RepeatMode
import net.asksakis.massdroidv2.domain.repository.MusicRepository
import net.asksakis.massdroidv2.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Controls a single MA player from the TV: transport + volume, targeting the
 * player id passed via navigation. Selecting it also makes it the app's current
 * player so library plays from the home land here.
 */
@HiltViewModel
class TvNowPlayingViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val musicRepository: MusicRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val playerId: String = checkNotNull(savedStateHandle["playerId"])

    val player: StateFlow<Player?> = playerRepository.players
        .map { list -> list.firstOrNull { it.playerId == playerId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Live playback position (seconds), interpolated by the :core ticker. */
    val elapsed: StateFlow<Double> = playerRepository.elapsedTime
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0.0)

    /** Selected queue state (shuffle/repeat live here, per MA). */
    val queueState: StateFlow<QueueState?> = playerRepository.queueState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        playerRepository.selectPlayer(playerId)
    }

    fun toggleShuffle() {
        val queue = queueState.value ?: return
        viewModelScope.launch {
            runCatching { musicRepository.shuffleQueue(queue.queueId, !queue.shuffleEnabled) }
        }
    }

    fun cycleRepeat() {
        val queue = queueState.value ?: return
        val next = when (queue.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        viewModelScope.launch { runCatching { musicRepository.repeatQueue(queue.queueId, next) } }
    }

    fun playPause() = viewModelScope.launch { playerRepository.playPause(playerId) }
    fun next() = viewModelScope.launch { playerRepository.next(playerId) }
    fun previous() = viewModelScope.launch { playerRepository.previous(playerId) }

    /** Seek by a relative delta (seconds), clamped to the track. */
    fun seekBy(deltaSec: Double) {
        val duration = player.value?.currentMedia?.duration ?: return
        if (duration <= 0.0) return
        val target = (elapsed.value + deltaSec).coerceIn(0.0, duration)
        viewModelScope.launch { playerRepository.seek(playerId, target) }
    }

    fun volumeUp() = changeVolume(VOLUME_STEP)
    fun volumeDown() = changeVolume(-VOLUME_STEP)

    private fun changeVolume(delta: Int) {
        val current = player.value?.volumeLevel ?: return
        viewModelScope.launch {
            playerRepository.setVolume(playerId, (current + delta).coerceIn(0, 100))
        }
    }

    private companion object {
        const val VOLUME_STEP = 5
    }
}
