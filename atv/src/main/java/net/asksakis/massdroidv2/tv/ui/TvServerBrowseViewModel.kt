package net.asksakis.massdroidv2.tv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.asksakis.massdroidv2.domain.model.BrowseItem as ServerBrowseItem
import net.asksakis.massdroidv2.domain.repository.MusicRepository
import net.asksakis.massdroidv2.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Server provider folder browse — the MA-UI "Browse" tree (RadioBrowser, ORF
 * Radiothek, Filesystem, Deezer…). Folders drill in via [MusicRepository.browse];
 * playable entries (radios, tracks, albums…) play on the selected player. Keeps
 * an in-screen path stack so Back pops one folder at a time.
 */
@HiltViewModel
class TvServerBrowseViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    private data class Crumb(val path: String?, val title: String)

    private val stack = ArrayDeque<Crumb>().apply { addLast(Crumb(null, "Browse")) }

    private val _entries = MutableStateFlow<List<ServerBrowseItem>>(emptyList())
    val entries: StateFlow<List<ServerBrowseItem>> = _entries.asStateFlow()

    private val _title = MutableStateFlow("Browse")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    init {
        load()
    }

    private fun load() {
        val crumb = stack.last()
        _title.value = crumb.title
        _canGoBack.value = stack.size > 1
        viewModelScope.launch {
            _loading.value = true
            _entries.value = runCatching { musicRepository.browse(crumb.path) }.getOrDefault(emptyList())
            _loading.value = false
        }
    }

    /** Open a folder, or play a playable entry. */
    fun open(item: ServerBrowseItem) {
        if (item.isFolder) {
            stack.addLast(Crumb(item.path ?: item.uri, item.name))
            _entries.value = emptyList()
            load()
        } else if (item.isPlayable) {
            play(item.uri)
        }
    }

    /** Pop one folder. Returns false at the root (let the screen exit). */
    fun back(): Boolean {
        if (stack.size <= 1) return false
        stack.removeLast()
        load()
        return true
    }

    private fun play(uri: String) {
        viewModelScope.launch {
            val target = playerRepository.selectedPlayer.value?.playerId
                ?: playerRepository.players.value.firstOrNull()?.playerId
                ?: return@launch
            playerRepository.selectPlayer(target)
            runCatching { musicRepository.playMedia(target, uri, option = "replace") }
        }
    }
}
