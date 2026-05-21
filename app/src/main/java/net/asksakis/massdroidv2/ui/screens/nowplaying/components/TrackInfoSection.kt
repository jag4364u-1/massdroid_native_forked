package net.asksakis.massdroidv2.ui.screens.nowplaying.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import net.asksakis.massdroidv2.domain.model.Track

/**
 * Now-Playing title / artist / album block. In compact (landscape) mode,
 * album and artist are inline on a single marquee line; in portrait they
 * stack. Tapping artist or album navigates to their detail screens
 * provided the queue track exposes an item id + provider.
 */
@Composable
internal fun TrackInfoSection(
    title: String,
    artist: String,
    album: String,
    currentTrack: Track?,
    onNavigateToArtist: (String, String, String) -> Unit,
    onNavigateToAlbum: (String, String, String) -> Unit,
    compact: Boolean = false
) {
    val titleStyle = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall
    val artistStyle = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.titleMedium
    val albumStyle = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.titleSmall
    val artistClickable = currentTrack?.artistItemId != null && currentTrack.artistProvider != null
    val albumClickable = currentTrack?.albumItemId != null && currentTrack.albumProvider != null

    Text(
        text = title,
        style = titleStyle,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .basicMarquee(iterations = Int.MAX_VALUE, velocity = 60.dp)
    )
    Spacer(modifier = Modifier.height(if (compact) 10.dp else 8.dp))

    if (compact && album.isNotBlank() && artist.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(iterations = Int.MAX_VALUE, velocity = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = album,
                style = albumStyle,
                color = if (albumClickable) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = if (albumClickable) Modifier.clickable {
                    onNavigateToAlbum(currentTrack.albumItemId!!, currentTrack.albumProvider!!, album)
                } else Modifier
            )
            Text(
                text = " · ",
                style = albumStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = artist,
                style = albumStyle,
                color = if (artistClickable) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = if (artistClickable) Modifier.clickable {
                    onNavigateToArtist(currentTrack.artistItemId!!, currentTrack.artistProvider!!, artist)
                } else Modifier
            )
        }
    } else {
        Text(
            text = artist,
            style = artistStyle,
            color = if (artistClickable) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = (if (artistClickable) {
                Modifier.clickable {
                    onNavigateToArtist(currentTrack.artistItemId!!, currentTrack.artistProvider!!, artist)
                }
            } else Modifier).fillMaxWidth()
        )
        if (album.isNotBlank()) {
            Spacer(modifier = Modifier.height(if (compact) 2.dp else 4.dp))
            Text(
                text = album,
                style = albumStyle,
                color = if (albumClickable) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (albumClickable) Modifier.clickable {
                    onNavigateToAlbum(currentTrack.albumItemId!!, currentTrack.albumProvider!!, album)
                } else Modifier
            )
        }
    }
}
