package net.asksakis.massdroidv2.data.sendspin

/**
 * Grouped (multi-device) Sendspin engine. Schedules every chunk against the
 * absolute group timeline plus a fixed scheduling headroom so all members play
 * each sample at `serverTime + headroom`, and gates the start on clock
 * convergence. The heavy lifting (decode, AudioTrack, queue, write loop) lives
 * in [SendspinPlaybackEngine]; this class only contributes the SYNC timing
 * policy.
 */
class SendspinSyncEngine : SendspinPlaybackEngine() {
    companion object {
        private const val SYNC_START_BUFFER_MS = 250L
        private const val SYNC_CLOCK_WAIT_MS = 3_000L
        private const val SYNC_CLOCK_ERROR_US = 15_000L
        private const val START_TARGET_HEADROOM_US = 50_000L
        // Positive scheduling headroom added to every chunk's local deadline,
        // matching sendspin-js SCHEDULE_HEADROOM_SEC = 0.2. Every Sendspin
        // client (web UI, Cast receiver, demo) plays each sample at
        // serverTime + 200ms, so all group members stay phase-locked while
        // absorbing one-way network latency. Without it the first frames of a
        // fresh stream arrive already past their deadline and get drop-stormed.
        private const val SCHEDULE_HEADROOM_US = 200_000L
    }

    override val correctionMode: CorrectionMode = CorrectionMode.SYNC
    override val startBufferMs: Long = SYNC_START_BUFFER_MS

    override fun computeLocalPlan(serverTimestampUs: Long, outputLatencyUs: Long): LocalPlan {
        val localOutputUs = clockSynchronizer?.serverToLocalUs(serverTimestampUs) ?: nowUs()
        return LocalPlan(localOutputUs, SCHEDULE_HEADROOM_US)
    }

    override fun startupGate(neededMs: Long): Boolean {
        val sync = clockSynchronizer ?: return false
        if (sync.isReadyForPlaybackStart()) return trimStartupLateFrames(neededMs, START_TARGET_HEADROOM_US)
        val now = System.currentTimeMillis()
        if (startupWaitStartedMs == 0L) startupWaitStartedMs = now
        val timedOutReady = now - startupWaitStartedMs >= SYNC_CLOCK_WAIT_MS &&
            sync.isSynced() &&
            sync.errorUs() <= SYNC_CLOCK_ERROR_US
        if (!timedOutReady) maybeLogStartupWait("clock", neededMs)
        return timedOutReady && trimStartupLateFrames(neededMs, START_TARGET_HEADROOM_US)
    }
}
