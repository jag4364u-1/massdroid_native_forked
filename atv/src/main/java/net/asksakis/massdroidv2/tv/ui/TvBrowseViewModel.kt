package net.asksakis.massdroidv2.tv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.asksakis.massdroidv2.domain.model.Album
import net.asksakis.massdroidv2.domain.model.Artist
import net.asksakis.massdroidv2.domain.model.Playlist
import net.asksakis.massdroidv2.domain.model.Radio
import net.asksakis.massdroidv2.domain.model.Track
import net.asksakis.massdroidv2.domain.repository.MusicRepository
import net.asksakis.massdroidv2.domain.repository.PlayerRepository
import javax.inject.Inject

enum class BrowseCategory(val label: String) {
    ARTISTS("Artists"),
    ALBUMS("Albums"),
    TRACKS("Tracks"),
    PLAYLISTS("Playlists"),
    RADIOS("Radios"),
    AUDIOBOOKS("Audiobooks"),
}

/** A library item rendered in the Browse grid, plus how a click should act. */
data class BrowseItem(
    val uri: String,
    val imageUrl: String?,
    val title: String,
    val subtitle: String?,
    val circular: Boolean = false,
    val isArtist: Boolean = false,
    val itemId: String = "",
    val provider: String = "",
)

/**
 * Full server library browse, ATV style: pick a category and scroll a paginated
 * grid of everything in it (no fixed cap). Albums/tracks/playlists play on the
 * selected player; artists drill into their albums.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TvBrowseViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
) : ViewModel() {

    private val artistsPager = LibraryPager(viewModelScope, { a: Artist -> a.uri }) { l, o ->
        musicRepository.getArtists(limit = l, offset = o, orderBy = "name")
    }
    private val albumsPager = LibraryPager(viewModelScope, { a: Album -> a.uri }) { l, o ->
        musicRepository.getAlbums(limit = l, offset = o, orderBy = "name")
    }
    private val tracksPager = LibraryPager(viewModelScope, { t: Track -> t.uri }) { l, o ->
        musicRepository.getTracks(limit = l, offset = o, orderBy = "name")
    }
    private val playlistsPager = LibraryPager(viewModelScope, { p: Playlist -> p.uri }) { l, o ->
        musicRepository.getPlaylists(limit = l, offset = o, orderBy = "name")
    }
    private val radiosPager = LibraryPager(viewModelScope, { r: Radio -> r.uri }) { l, o ->
        musicRepository.getRadios(limit = l, offset = o, orderBy = "name")
    }
    private val audiobooksPager = LibraryPager(viewModelScope, { t: Track -> t.uri }) { l, o ->
        musicRepository.getAudiobooks(limit = l, offset = o, orderBy = "name")
    }

    private val artistItems = artistsPager.items.map { list ->
        list.map { BrowseItem(it.uri, it.imageUrl, it.name, null, circular = true, isArtist = true, itemId = it.itemId, provider = it.provider) }
    }
    private val albumItems = albumsPager.items.map { list ->
        list.map { BrowseItem(it.uri, it.imageUrl, it.name, it.artistNames) }
    }
    private val trackItems = tracksPager.items.map { list ->
        list.map { BrowseItem(it.uri, it.imageUrl, it.name, it.artistNames) }
    }
    private val playlistItems = playlistsPager.items.map { list ->
        list.map { BrowseItem(it.uri, it.imageUrl, it.name, null) }
    }
    private val radioItems = radiosPager.items.map { list ->
        list.map { BrowseItem(it.uri, it.imageUrl, it.name, null) }
    }
    private val audiobookItems = audiobooksPager.items.map { list ->
        list.map { BrowseItem(it.uri, it.imageUrl, it.name, it.artistNames.takeIf { n -> n.isNotBlank() }) }
    }

    private val _category = MutableStateFlow(BrowseCategory.ARTISTS)
    val category: StateFlow<BrowseCategory> = _category.asStateFlow()

    val items: StateFlow<List<BrowseItem>> = _category
        .flatMapLatest { cat ->
            when (cat) {
                BrowseCategory.ARTISTS -> artistItems
                BrowseCategory.ALBUMS -> albumItems
                BrowseCategory.TRACKS -> trackItems
                BrowseCategory.PLAYLISTS -> playlistItems
                BrowseCategory.RADIOS -> radioItems
                BrowseCategory.AUDIOBOOKS -> audiobookItems
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        artistsPager.loadMore()
    }

    fun selectCategory(cat: BrowseCategory) {
        _category.value = cat
        pagerFor(cat).loadMoreIfEmpty()
    }

    /** Fetch the next page of the current category as the grid nears its end. */
    fun loadMore() = pagerFor(_category.value).loadMore()

    fun play(uri: String) {
        viewModelScope.launch {
            val target = playerRepository.selectedPlayer.value?.playerId
                ?: playerRepository.players.value.firstOrNull()?.playerId
                ?: return@launch
            playerRepository.selectPlayer(target)
            runCatching { musicRepository.playMedia(target, uri, option = "replace") }
        }
    }

    private fun pagerFor(cat: BrowseCategory): LibraryPager<*> = when (cat) {
        BrowseCategory.ARTISTS -> artistsPager
        BrowseCategory.ALBUMS -> albumsPager
        BrowseCategory.TRACKS -> tracksPager
        BrowseCategory.PLAYLISTS -> playlistsPager
        BrowseCategory.RADIOS -> radiosPager
        BrowseCategory.AUDIOBOOKS -> audiobooksPager
    }
}
