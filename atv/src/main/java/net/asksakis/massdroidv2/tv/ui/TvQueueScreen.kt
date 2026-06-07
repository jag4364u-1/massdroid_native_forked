package net.asksakis.massdroidv2.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

/** Overscan-safe horizontal inset for 10-foot layout. */
private val EDGE = 56.dp

/** 10-foot view of the active queue. Current track highlighted; click plays that index. */
@Composable
fun TvQueueScreen(viewModel: TvQueueViewModel = hiltViewModel()) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val currentId by viewModel.currentItemId.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 40.dp)) {
            Text(
                "Queue",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = EDGE)
            )
            Spacer(Modifier.height(20.dp))
            if (items.isEmpty()) {
                Text(
                    "Queue is empty",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = EDGE)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = EDGE, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(items, key = { _, it -> it.queueItemId }) { index, item ->
                        QueueRow(
                            title = item.name.ifBlank { item.track?.name.orEmpty() },
                            subtitle = item.track?.artistNames?.takeIf { it.isNotBlank() }
                                ?: item.track?.albumName.orEmpty(),
                            imageUrl = item.imageUrl,
                            isCurrent = item.queueItemId == currentId,
                            onClick = { viewModel.playIndex(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QueueRow(
    title: String,
    subtitle: String,
    imageUrl: String?,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        // Colors inherit the Surface's focus-aware content color (inverts on
        // focus) so the row stays readable focused or not. "Current" is shown by
        // a bold title + equalizer glyph, not a hardcoded bright color.
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (isCurrent) {
                Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.GraphicEq, contentDescription = "Now playing")
                }
            }
        }
    }
}
