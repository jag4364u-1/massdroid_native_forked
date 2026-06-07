package net.asksakis.massdroidv2.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import net.asksakis.massdroidv2.domain.model.Player

@Composable
fun TvHomeScreen(
    onOpenPlayer: (String) -> Unit,
    viewModel: TvHomeViewModel = hiltViewModel()
) {
    val players by viewModel.players.collectAsStateWithLifecycle()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsStateWithLifecycle()
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val artists by viewModel.artists.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 48.dp, end = 48.dp, top = 40.dp, bottom = 48.dp)
        ) {
            Text("MassDroid TV", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(28.dp))

            if (players.isNotEmpty()) {
                Shelf("Players") {
                    items(players, key = { it.playerId }) { p -> PlayerCard(p) { onOpenPlayer(p.playerId) } }
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
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 48.dp),
            content = content
        )
    }
}

@Composable
private fun PlayerCard(player: Player, onClick: () -> Unit) {
    val subtitle = player.currentMedia?.title?.takeIf { it.isNotBlank() }
        ?: player.state.name.lowercase().replaceFirstChar { it.uppercase() }
    Card(onClick = onClick, modifier = Modifier.width(280.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(player.displayName, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
