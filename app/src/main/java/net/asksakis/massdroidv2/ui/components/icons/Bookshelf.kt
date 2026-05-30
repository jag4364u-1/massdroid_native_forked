package net.asksakis.massdroidv2.ui.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * Material Design Icons `bookshelf` glyph (Pictogrammers, Apache 2.0), embedded so our library
 * add/remove affordances visually match the Music Assistant web UI "available in library"
 * indicator. The Android Material Icons set has no bookshelf glyph, so we carry the path here.
 */
val Icons.Filled.Bookshelf: ImageVector
    get() = cached ?: ImageVector.Builder(
        name = "Filled.Bookshelf",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        addPath(
            pathData = PathParser()
                .parsePathString("M9 3V18H12V3H9M12 5L16 18L19 17L15 4L12 5M5 5V18H8V5H5M3 19V21H21V19H3Z")
                .toNodes(),
            fill = SolidColor(Color.Black)
        )
    }.build().also { cached = it }

private var cached: ImageVector? = null
