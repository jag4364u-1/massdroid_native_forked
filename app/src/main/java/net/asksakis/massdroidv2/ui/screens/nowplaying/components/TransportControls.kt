package net.asksakis.massdroidv2.ui.screens.nowplaying.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.asksakis.massdroidv2.domain.model.QueueState
import net.asksakis.massdroidv2.domain.model.RepeatMode
import net.asksakis.massdroidv2.ui.components.MdIconButton
import net.asksakis.massdroidv2.ui.screens.nowplaying.NowPlayingViewModel

/**
 * Shuffle / Previous / Play-Pause / Next / Repeat row. Pure UI projection
 * of the queue/transport state — calls into the ViewModel for the actual
 * commands. Compact mode shrinks the row for the landscape layout.
 */
@Composable
internal fun TransportControls(
    isPlaying: Boolean,
    queueState: QueueState?,
    viewModel: NowPlayingViewModel,
    enabled: Boolean = true,
    compact: Boolean = false,
    onHaptic: () -> Unit = {}
) {
    val buttonSize = if (compact) 40.dp else 48.dp
    val playSize = if (compact) 52.dp else 64.dp
    val iconSize = if (compact) 26.dp else 32.dp
    val playIconSize = if (compact) 30.dp else 36.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MdIconButton(onClick = {
            onHaptic()
            viewModel.toggleShuffle()
        }, enabled = enabled) {
            Icon(
                Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (queueState?.shuffleEnabled == true)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        MdIconButton(onClick = {
            onHaptic()
            viewModel.previous()
        }, modifier = Modifier.size(buttonSize), enabled = enabled) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(iconSize))
        }

        FilledIconButton(onClick = {
            onHaptic()
            viewModel.playPause()
        }, modifier = Modifier.size(playSize), enabled = enabled) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(playIconSize)
            )
        }

        MdIconButton(onClick = {
            onHaptic()
            viewModel.next()
        }, modifier = Modifier.size(buttonSize), enabled = enabled) {
            Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(iconSize))
        }

        MdIconButton(onClick = {
            onHaptic()
            viewModel.cycleRepeat()
        }, enabled = enabled) {
            Icon(
                when (queueState?.repeatMode) {
                    RepeatMode.ONE -> Icons.Default.RepeatOne
                    else -> Icons.Default.Repeat
                },
                contentDescription = "Repeat",
                tint = if (queueState?.repeatMode != RepeatMode.OFF)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
