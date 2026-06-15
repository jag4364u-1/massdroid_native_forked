package net.asksakis.massdroidv2.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import net.asksakis.massdroidv2.tv.R
import androidx.tv.material3.Card
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

@Composable
fun TvHomeScreen(
    onOpenSettings: () -> Unit,
    onOpenArtist: (itemId: String, provider: String) -> Unit,
    onOpenBrowse: () -> Unit,
    viewModel: TvHomeViewModel = hiltViewModel()
) {
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

    // Back-stack focus restoration: remember the last focused card key (survives the
    // detail screen via rememberSaveable) and return focus there when home recomposes,
    // so Back lands you where you were instead of jumping to a player. The row/scroll
    // positions restore on their own (LazyListState/ScrollState are saveable).
    var lastFocusedKey by rememberSaveable { mutableStateOf<String?>(null) }
    val restoreRequester = remember { FocusRequester() }
    // The mini player asks us (via TvFocusMemory) to put the cursor back on the card it
    // stole it from; the same key/requester pair already serves back-stack restoration.
    val focusMemory = LocalTvFocusMemory.current
    DisposableEffect(focusMemory) {
        val hook: () -> Boolean = {
            lastFocusedKey != null && runCatching { restoreRequester.requestFocus() }.isSuccess
        }
        focusMemory.restoreToLastFocused = hook
        // Only clear our own hook: on navigation the NEW screen registers before the old
        // one disposes, and an unconditional null here would wipe the new screen's hook.
        onDispose {
            if (focusMemory.restoreToLastFocused === hook) focusMemory.restoreToLastFocused = null
        }
    }
    val focusModifierFor: (String) -> Modifier = { key ->
        Modifier
            .onFocusChanged { if (it.isFocused) lastFocusedKey = key }
            .then(if (key == lastFocusedKey) Modifier.focusRequester(restoreRequester) else Modifier)
    }
    LaunchedEffect(Unit) {
        if (lastFocusedKey == null) return@LaunchedEffect
        // The target card composes once its row's saved scroll position restores;
        // retry until the requester is attached.
        repeat(FOCUS_RESTORE_TRIES) {
            if (runCatching { restoreRequester.requestFocus() }.isSuccess) return@LaunchedEffect
            delay(FOCUS_RESTORE_RETRY_MS)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 40.dp, bottom = 48.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = EDGE),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.logo_md),
                        contentDescription = null,
                        // The logo_md vector has internal padding, so render it a bit
                        // larger than the cap height to visually match the wordmark.
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("MassDroid TV", style = MaterialTheme.typography.headlineMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onOpenBrowse) {
                        Icon(Icons.Filled.GridView, contentDescription = "Browse")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            }
            Spacer(Modifier.height(28.dp))

            ContentShelf(
                "Recently Played",
                recentlyPlayed.map { MediaCardData(it.uri, it.imageUrl, it.name, it.artistNames) },
                onClick = { viewModel.playMedia(it.uri) },
                focusModifierFor = focusModifierFor
            )
            ContentShelf(
                "Albums",
                albums.map { MediaCardData(it.uri, it.imageUrl, it.name, it.artistNames) },
                onClick = { viewModel.playMedia(it.uri) },
                onLoadMore = viewModel::loadMoreAlbums,
                focusModifierFor = focusModifierFor
            )
            ContentShelf(
                "Artists",
                artists.map { MediaCardData(it.uri, it.imageUrl, it.name, null, it.itemId, it.provider) },
                onClick = { onOpenArtist(it.itemId, it.provider) },
                circular = true,
                onLoadMore = viewModel::loadMoreArtists,
                focusModifierFor = focusModifierFor
            )
            ContentShelf(
                "Playlists",
                playlists.map { MediaCardData(it.uri, it.imageUrl, it.name, null) },
                onClick = { viewModel.playMedia(it.uri) },
                focusModifierFor = focusModifierFor
            )
        }
    }
}

private data class MediaCardData(
    val uri: String,
    val imageUrl: String?,
    val title: String,
    val subtitle: String?,
    val itemId: String = "",
    val provider: String = ""
)

@Composable
private fun ContentShelf(
    title: String,
    items: List<MediaCardData>,
    onClick: (MediaCardData) -> Unit,
    circular: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    focusModifierFor: (String) -> Modifier = { Modifier }
) {
    if (items.isEmpty()) return
    val state = rememberLazyListState()
    // Paginate: pull the next page as focus/scroll nears the end of the row.
    if (onLoadMore != null) {
        LaunchedEffect(state, items.size) {
            snapshotFlow { state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
                .collect { lastVisible ->
                    if (lastVisible >= items.size - LOAD_MORE_THRESHOLD) onLoadMore()
                }
        }
    }
    Shelf(title, state) {
        items(items, key = { it.uri }) { item ->
            MediaCard(item, circular, modifier = focusModifierFor(item.uri)) { onClick(item) }
        }
    }
}

private const val LOAD_MORE_THRESHOLD = 10
private const val FOCUS_RESTORE_TRIES = 25
private const val FOCUS_RESTORE_RETRY_MS = 60L

@Composable
private fun Shelf(
    title: String,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = EDGE)
        )
        Spacer(Modifier.height(12.dp))
        // Overscan-safe inset lives in the row's contentPadding (not the parent),
        // so focused edge cards can scale without being clipped at the screen edge.
        LazyRow(
            state = state,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = EDGE),
            content = content
        )
    }
}

/** Overscan-safe horizontal inset for 10-foot layout. */
private val EDGE = 56.dp

/** Home row card/art size. Kept compact so more items are visible per row. */
private val CARD_SIZE = 116.dp

@Composable
private fun MediaCard(
    item: MediaCardData,
    circular: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = modifier.width(CARD_SIZE)) {
        Column {
            val imageModifier = if (circular) {
                Modifier.size(CARD_SIZE).clip(CircleShape)
            } else {
                Modifier.size(CARD_SIZE)
            }
            Box(modifier = Modifier.size(CARD_SIZE)) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }
            Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                Text(item.title, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (item.subtitle != null) {
                    Text(
                        item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
