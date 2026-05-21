package net.asksakis.massdroidv2.data.util

import kotlinx.coroutines.delay

/**
 * Throttling helpers for code that iterates over a list and issues an
 * MA `sendCommand` per item (similar-artist resolution, library
 * enrichment fallback, etc.).
 *
 * The Music Assistant server runs on a single event loop, so even a
 * sequential 30-item resolver loop without pacing will starve
 * latency-sensitive commands (seek/play/pause/Sendspin streaming) of
 * server time. Production logs showed a 12-second seek lag triggered
 * by 47 back-to-back `music/search` + `music/artists/get` RPCs from
 * `LibraryViewModel.loadSimilarArtists`; the fix is to space these
 * out so the WS pipeline can interleave priority traffic.
 *
 * [MA_BULK_THROTTLE_MS] is the gap we pause between successive bulk
 * MA RPCs. Chosen empirically: short enough that 8 similar-artist
 * resolutions finish in ~1.2 s instead of feeling laggy in the UI,
 * long enough to keep the server's per-second budget under 10 RPCs
 * from this client even on a cold Last.fm path.
 */
const val MA_BULK_THROTTLE_MS = 150L

/**
 * Sequentially map [block] over [items] with a [MA_BULK_THROTTLE_MS]
 * pause between each call. The first call fires immediately; pauses
 * are inserted only between subsequent calls so we don't add latency
 * to the single-item case.
 *
 * Use this in place of plain `items.map { block(it) }` whenever
 * [block] issues an MA `sendCommand` and the list size can exceed 3.
 */
suspend fun <T, R> Iterable<T>.mapMaThrottled(
    block: suspend (T) -> R,
): List<R> {
    val src = this.toList()
    if (src.isEmpty()) return emptyList()
    val out = ArrayList<R>(src.size)
    src.forEachIndexed { index, item ->
        if (index > 0) delay(MA_BULK_THROTTLE_MS)
        out.add(block(item))
    }
    return out
}
