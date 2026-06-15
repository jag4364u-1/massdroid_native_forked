package net.asksakis.massdroidv2.data.lastfm

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Global rate limiter for ALL Last.fm API calls.
 *
 * Every resolver acquires a slot before hitting the network, so concurrent
 * callers (Smart Mix expansion prefetch, library enrichment, detail-screen
 * lookups) collectively stay under Last.fm's limit (~5 req/s) instead of each
 * enforcing its own ad-hoc throttle. Being a singleton acquired right before the
 * HTTP call makes it impossible for a caller to bypass.
 */
@Singleton
class LastFmRateLimiter @Inject constructor() {
    private val mutex = Mutex()
    private var nextAllowedAt = 0L

    /** Suspends until the caller is allowed to issue its Last.fm request. */
    suspend fun acquire() {
        mutex.withLock {
            val now = System.currentTimeMillis()
            val waitMs = nextAllowedAt - now
            if (waitMs > 0) delay(waitMs)
            nextAllowedAt = maxOf(now, nextAllowedAt) + MIN_INTERVAL_MS
        }
    }

    private companion object {
        // ~5 requests/second across the whole app.
        const val MIN_INTERVAL_MS = 200L
    }
}
