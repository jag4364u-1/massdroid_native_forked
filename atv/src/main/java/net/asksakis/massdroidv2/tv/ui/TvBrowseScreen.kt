package net.asksakis.massdroidv2.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.tv.material3.Card
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

/** Full server library browse: category chips + a paginated grid of everything. */
@Composable
fun TvBrowseScreen(
    onOpenArtist: (itemId: String, provider: String) -> Unit,
    onOpenFolders: () -> Unit,
    viewModel: TvBrowseViewModel = hiltViewModel()
) {
    val category by viewModel.category.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()

    LaunchedEffect(gridState, items.size) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1 }
            .collect { last -> if (last >= items.size - BROWSE_LOAD_MORE_THRESHOLD) viewModel.loadMore() }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 24.dp)) {
            Text(
                "Browse",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = BROWSE_EDGE)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.padding(horizontal = BROWSE_EDGE),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BrowseCategory.entries.forEach { cat ->
                    CategoryChip(cat.label, cat == category) { viewModel.selectCategory(cat) }
                }
                // Provider folder tree (RadioBrowser, ORF, Filesystem…) — the MA-UI
                // "Browse". Not a flat category, so it opens its own drill-down screen.
                CategoryChip("Folders", selected = false, onClick = onOpenFolders)
            }
            Spacer(Modifier.height(16.dp))
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(BROWSE_GRID_COLUMNS),
                contentPadding = PaddingValues(horizontal = BROWSE_EDGE, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(items, key = { it.uri }) { item ->
                    BrowseCard(item) {
                        if (item.isArtist) onOpenArtist(item.itemId, item.provider)
                        else viewModel.play(item.uri)
                    }
                }
            }
        }
    }
}

private val BROWSE_EDGE = 56.dp
private const val BROWSE_GRID_COLUMNS = 6
private const val BROWSE_LOAD_MORE_THRESHOLD = 12

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) {
        ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ClickableSurfaceDefaults.colors()
    }
    Surface(
        onClick = onClick,
        colors = colors,
        shape = ClickableSurfaceDefaults.shape(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun BrowseCard(item: BrowseItem, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column {
            val imageModifier = Modifier.fillMaxWidth().aspectRatio(1f)
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = if (item.circular) imageModifier.clip(CircleShape) else imageModifier
            )
            Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.subtitle != null && item.subtitle.isNotBlank()) {
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
