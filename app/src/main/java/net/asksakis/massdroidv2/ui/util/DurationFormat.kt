package net.asksakis.massdroidv2.ui.util

/**
 * Format a playback time (seconds) as `m:ss`, switching to `H:MM:SS` for long content.
 *
 * The switch is content-length driven, not media-type gated: audiobooks, long podcasts,
 * DJ sets and >1h tracks all benefit. Pass [padToHours] to force the long form so an
 * elapsed label stays aligned with a >= 1h total (e.g. `0:04:32 / 2:58:52`).
 */
fun formatPlaybackTime(seconds: Double, padToHours: Boolean = false): String {
    val total = seconds.toInt().coerceAtLeast(0)
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0 || padToHours) {
        "%d:%02d:%02d".format(h, m, s)
    } else {
        "%d:%02d".format(m, s)
    }
}

/** True when a duration (seconds) should render in `H:MM:SS` form. */
fun isLongFormDuration(seconds: Double): Boolean = seconds >= 3600.0
