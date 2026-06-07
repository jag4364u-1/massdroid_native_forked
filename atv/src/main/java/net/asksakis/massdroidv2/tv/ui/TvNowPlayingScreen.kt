package net.asksakis.massdroidv2.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import net.asksakis.massdroidv2.domain.model.PlaybackState

@Composable
fun TvNowPlayingScreen(viewModel: TvNowPlayingViewModel = hiltViewModel()) {
    val player by viewModel.player.collectAsStateWithLifecycle()
    val media = player?.currentMedia

    Surface(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            AsyncImage(
                model = media?.imageUrl,
                contentDescription = media?.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(340.dp)
            )

            Column(modifier = Modifier.width(640.dp)) {
                Text(
                    player?.displayName ?: "Player",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    media?.title?.takeIf { it.isNotBlank() } ?: "Nothing playing",
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    media?.artist.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { viewModel.previous() }) { Text("Previous") }
                    Button(onClick = { viewModel.playPause() }) {
                        Text(if (player?.state == PlaybackState.PLAYING) "Pause" else "Play")
                    }
                    Button(onClick = { viewModel.next() }) { Text("Next") }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { viewModel.volumeDown() }) { Text("Vol -") }
                    Button(onClick = { viewModel.volumeUp() }) { Text("Vol +") }
                    Text(
                        "${player?.volumeLevel ?: 0}%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
