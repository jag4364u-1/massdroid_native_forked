package net.asksakis.massdroidv2.tv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.asksakis.massdroidv2.domain.model.QueueItem
import net.asksakis.massdroidv2.domain.repository.MusicRepository
import net.asksakis.massdroidv2.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Read + play view of the selected player's active queue. Items come from the
 * shared :core [PlayerRepository.queueItems] snapshot (one RPC per queue change,
 * shared with every other consumer); tapping a row plays that index.
 */
@HiltViewModel
class TvQueueViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val musicRepository: MusicRepository,
) : ViewModel() {

    // Only surface items whose snapshot matches the currently selected queue, so
    // a stale snapshot from a previous player never leaks in during a switch.
    val items: StateFlow<List<QueueItem>> =
        combine(playerRepository.queueItems, playerRepository.queueState) { snapshot, queue ->
            val queueId = queue?.queueId
            if (snapshot != null && queueId != null && snapshot.queueId == queueId) {
                snapshot.items
            } else {
                emptyList()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** queueItemId of the currently playing item, for highlighting. */
    val currentItemId: StateFlow<String?> = playerRepository.queueState
        .map { it?.currentItem?.queueItemId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        // Force a fresh fetch whenever the selected queue id becomes available or
        // changes. The coordinator already fetches on player switch; this also
        // covers the case where the snapshot is stale/missing when we open.
        viewModelScope.launch {
            playerRepository.queueState
                .map { it?.queueId }
                .distinctUntilChanged()
                .filterNotNull()
                .collect { queueId -> runCatching { playerRepository.refreshQueueItems(queueId) } }
        }
    }

    fun playIndex(index: Int) {
        val queueId = playerRepository.queueState.value?.queueId ?: return
        viewModelScope.launch { runCatching { musicRepository.playQueueIndex(queueId, index) } }
    }
}
