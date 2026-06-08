package net.asksakis.massdroidv2.tv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.asksakis.massdroidv2.domain.model.Album
import net.asksakis.massdroidv2.domain.model.Artist
import net.asksakis.massdroidv2.domain.model.Player
import net.asksakis.massdroidv2.domain.model.Playlist
import net.asksakis.massdroidv2.domain.repository.MusicRepository
import net.asksakis.massdroidv2.domain.repository.PlayerRepository
import net.asksakis.massdroidv2.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Connected home content, ATV style: live players plus library shelves pulled
 * straight from the shared :core repositories and rendered as D-pad card rows.
 *
 * Albums and Artists are paginated so the whole library is reachable: each row
 * fetches the next page as you scroll near its end, with no fixed cap. Recently
 * Played and Playlists stay as bounded previews.
 */
@HiltViewModel
class TvHomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
    settingsRepository: SettingsRepository,
) : ViewModel() {

    val players: StateFlow<List<Player>> = playerRepository.players
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** MA player id of this device's own Sendspin player (the local "MassDroid TV"). */
    val localPlayerId: StateFlow<String?> = settingsRepository.sendspinClientId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val selectedPlayerId: StateFlow<String?> = playerRepository.selectedPlayer
        .map { it?.playerId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun selectPlayer(playerId: String) = playerRepository.selectPlayer(playerId)

    private val _recentlyPlayed = MutableStateFlow<List<Album>>(emptyList())
    val recentlyPlayed: StateFlow<List<Album>> = _recentlyPlayed.asStateFlow()

    private val albumsPager = LibraryPager(viewModelScope, { a: Album -> a.uri }) { limit, offset ->
        musicRepository.getAlbums(limit = limit, offset = offset, orderBy = "name")
    }
    val albums: StateFlow<List<Album>> = albumsPager.items

    private val artistsPager = LibraryPager(viewModelScope, { a: Artist -> a.uri }) { limit, offset ->
        musicRepository.getArtists(limit = limit, offset = offset, orderBy = "name")
    }
    val artists: StateFlow<List<Artist>> = artistsPager.items

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    init {
        load(_recentlyPlayed) { musicRepository.getAlbums(limit = 25, orderBy = "last_played_desc") }
        load(_playlists) { musicRepository.getPlaylists(limit = 100) }
        albumsPager.loadMore()
        artistsPager.loadMore()
    }

    /** Fetch the next page of albums when the row nears its end. */
    fun loadMoreAlbums() = albumsPager.loadMore()

    /** Fetch the next page of artists when the row nears its end. */
    fun loadMoreArtists() = artistsPager.loadMore()

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
