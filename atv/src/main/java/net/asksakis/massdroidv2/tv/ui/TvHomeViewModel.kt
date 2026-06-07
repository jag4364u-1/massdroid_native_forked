package net.asksakis.massdroidv2.tv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.asksakis.massdroidv2.domain.model.Album
import net.asksakis.massdroidv2.domain.model.Artist
import net.asksakis.massdroidv2.domain.model.Player
import net.asksakis.massdroidv2.domain.model.Playlist
import net.asksakis.massdroidv2.domain.repository.MusicRepository
import net.asksakis.massdroidv2.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Connected home content, ATV style: live players plus library shelves pulled
 * straight from the shared :core repositories and rendered as D-pad card rows.
 */
@HiltViewModel
class TvHomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    val players: StateFlow<List<Player>> = playerRepository.players
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _recentlyPlayed = MutableStateFlow<List<Album>>(emptyList())
    val recentlyPlayed: StateFlow<List<Album>> = _recentlyPlayed.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    init {
        load(_recentlyPlayed) { musicRepository.getAlbums(limit = 20, orderBy = "last_played") }
        load(_albums) { musicRepository.getAlbums(limit = 20, orderBy = "name") }
        load(_artists) { musicRepository.getArtists(limit = 20, orderBy = "name") }
        load(_playlists) { musicRepository.getPlaylists(limit = 20) }
    }

    /** Play a library item on the selected player (or the first available one). */
    fun playMedia(uri: String) {
        viewModelScope.launch {
            val target = playerRepository.selectedPlayer.value?.playerId
                ?: players.value.firstOrNull()?.playerId
                ?: return@launch
            playerRepository.selectPlayer(target)
            runCatching { musicRepository.playMedia(target, uri, option = "replace") }
        }
    }

    private fun <T> load(target: MutableStateFlow<List<T>>, block: suspend () -> List<T>) {
        viewModelScope.launch { runCatching { block() }.onSuccess { target.value = it } }
    }
}
