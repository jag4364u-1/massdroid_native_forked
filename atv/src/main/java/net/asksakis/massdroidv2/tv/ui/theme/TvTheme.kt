package net.asksakis.massdroidv2.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

// Aligned with the phone app's grayscale dark scheme (see app Theme.kt) so the
// TV front-end shares the same monochrome brand look.
private val MassDroidTvColors = darkColorScheme(
    primary = Color(0xFFCCCCCC),
    onPrimary = Color(0xFF222222),
    primaryContainer = Color(0xFF3A3A3A),
    onPrimaryContainer = Color(0xFFDDDDDD),
    secondary = Color(0xFFAAAAAA),
    onSecondary = Color(0xFF222222),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFAAAAAA),
    border = Color(0xFF666666),
)

/** Dark grayscale theme matching the phone app. */
@Composable
fun MassDroidTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MassDroidTvColors, content = content)
}
