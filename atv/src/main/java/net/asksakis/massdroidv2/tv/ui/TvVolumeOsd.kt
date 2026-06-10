package net.asksakis.massdroidv2.tv.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import kotlinx.coroutines.delay

private const val OSD_VISIBLE_MS = 1_800L
private const val OSD_FADE_MS = 250

/**
 * Volume on-screen display: pops whenever the selected player's volume changes (from the
 * mini player buttons, the full player, or another MA client) and fades away on its own.
 * Purely informational: never focusable, never intercepts keys.
 */
@Composable
fun TvVolumeOsd(
    modifier: Modifier = Modifier,
    viewModel: TvMiniPlayerViewModel = hiltViewModel()
) {
    val player by viewModel.selectedPlayer.collectAsStateWithLifecycle()

    var visible by remember { mutableStateOf(false) }
    var shownVolume by remember { mutableIntStateOf(0) }
    var lastPlayerId by remember { mutableStateOf<String?>(null) }
    var lastVolume by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(player?.playerId, player?.volumeLevel) {
        val current = player ?: return@LaunchedEffect
        // A player switch (or first observation) seeds the baseline silently.
        if (current.playerId != lastPlayerId || lastVolume == null) {
            lastPlayerId = current.playerId
            lastVolume = current.volumeLevel
            return@LaunchedEffect
        }
        if (current.volumeLevel == lastVolume) return@LaunchedEffect
        lastVolume = current.volumeLevel
        shownVolume = current.volumeLevel
        visible = true
        delay(OSD_VISIBLE_MS)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(androidx.compose.animation.core.tween(OSD_FADE_MS)),
        exit = fadeOut(androidx.compose.animation.core.tween(OSD_FADE_MS)),
        modifier = modifier
    ) {
        Surface(shape = RoundedCornerShape(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(shownVolume / 100f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text("$shownVolume", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
