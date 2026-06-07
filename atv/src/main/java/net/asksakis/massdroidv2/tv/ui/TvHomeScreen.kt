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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import net.asksakis.massdroidv2.domain.model.PlaybackState
import net.asksakis.massdroidv2.domain.model.Player

@Composable
fun TvHomeScreen(
    onOpenPlayer: (String) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: TvHomeViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val selectedPlayerId by viewModel.selectedPlayerId.collectAsStateWithLifecycle()
    val localPlayerId by viewModel.localPlayerId.collectAsStateWithLifecycle()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

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
                Text("MassDroid TV", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings")
                }
            }
            Spacer(Modifier.height(28.dp))

            if (players.isNotEmpty()) {
                Shelf("Players") {
                    items(players, key = { it.playerId }) { p ->
                        // First press selects the player as the playback target;
                        // pressing the already-selected one opens its controls.
                        PlayerCard(
                            p,
                            selected = p.playerId == selectedPlayerId,
                            local = p.playerId == localPlayerId
                        ) {
                            if (p.playerId == selectedPlayerId) onOpenPlayer(p.playerId)
                            else viewModel.selectPlayer(p.playerId)
                        }
                    }
                }
            }
            ContentShelf("Recently Played", recentlyPlayed.map { MediaCardData(it.uri, it.imageUrl, it.name, it.artistNames) }, viewModel::playMedia)
            ContentShelf("Albums", albums.map { MediaCardData(it.uri, it.imageUrl, it.name, it.artistNames) }, viewModel::playMedia)
            ContentShelf("Artists", artists.map { MediaCardData(it.uri, it.imageUrl, it.name, null) }, viewModel::playMedia, circular = true)
            ContentShelf("Playlists", playlists.map { MediaCardData(it.uri, it.imageUrl, it.name, null) }, viewModel::playMedia)
        }
    }
}

private data class MediaCardData(
    val uri: String,
    val imageUrl: String?,
    val title: String,
    val subtitle: String?
)

@Composable
private fun ContentShelf(
    title: String,
    items: List<MediaCardData>,
    onClick: (String) -> Unit,
    circular: Boolean = false
) {
    if (items.isEmpty()) return
    Shelf(title) {
        items(items, key = { it.uri }) { item ->
            MediaCard(item, circular) { onClick(item.uri) }
        }
    }
}

@Composable
private fun Shelf(title: String, content: LazyListScope.() -> Unit) {
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = EDGE),
            content = content
        )
    }
}

/** Overscan-safe horizontal inset for 10-foot layout. */
private val EDGE = 56.dp

@Composable
private fun PlayerCard(player: Player, selected: Boolean, local: Boolean, onClick: () -> Unit) {
    val subtitle = player.currentMedia?.title?.takeIf { it.isNotBlank() }
        ?: player.state.name.lowercase().replaceFirstChar { it.uppercase() }
    // Selection highlights the whole card (primary border) rather than a text label.
    val selectedBorder = CardDefaults.border(
        border = Border(
            border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        )
    )
    Card(
        onClick = onClick,
        border = if (selected) selectedBorder else CardDefaults.border(),
        modifier = Modifier.width(280.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                // Selection is shown by the card border, playing by the
                // equalizer glyph, local by the red badge. Text/icon inherit the
                // focus-aware content color so they stay readable when focused.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (player.state == PlaybackState.PLAYING) {
                        Icon(
                            Icons.Filled.GraphicEq,
                            contentDescription = "Playing",
                            modifier = Modifier.padding(end = 6.dp).size(18.dp)
                        )
                    }
                    Text(
                        player.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (local) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFD32F2F))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        "Local Player",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaCard(item: MediaCardData, circular: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.width(180.dp)) {
        Column {
            val imageModifier = if (circular) {
                Modifier.size(180.dp).clip(CircleShape)
            } else {
                Modifier.size(180.dp)
            }
            Box(modifier = Modifier.size(180.dp)) {
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
