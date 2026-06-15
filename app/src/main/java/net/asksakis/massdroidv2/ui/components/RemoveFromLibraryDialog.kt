package net.asksakis.massdroidv2.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Confirmation for the destructive `music/library/remove_item` action.
 *
 * Mirrors the Music Assistant web UI, which always confirms a library removal because the
 * server call is recursive: removing an album also removes its tracks, removing an artist
 * removes its albums and tracks.
 */
@Composable
fun RemoveFromLibraryDialog(
    itemTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove from Library") },
        text = { Text("Remove \"$itemTitle\" from your library? This also removes any items it contains.") },
        confirmButton = {
            MdTextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Remove", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            MdTextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
