package net.asksakis.massdroidv2.data.util

import android.os.SystemClock
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * View-agnostic signal that the server rejected a command because the logged-in user lacks the
 * required role. Since MA 2.9.0 a set of write commands (create/remove group player, save player
 * config, ...) require an admin account and return error_code 22 (InsufficientPermissions) for
 * everyone else. The WS client reports here from the single error choke point, so any current or
 * future admin-gated command is covered without per-call-site plumbing, and one app-level consumer
 * surfaces a friendly notice. Debounced so a burst produces at most one notice.
 */
@Singleton
class AccountNoticeReporter @Inject constructor() {

    private val _permissionDenied = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val permissionDenied: SharedFlow<Unit> = _permissionDenied.asSharedFlow()

    @Volatile
    private var lastEmitElapsed = -COOLDOWN_MS

    /** Report that a command was rejected for insufficient role. Debounced to one notice per cooldown. */
    fun reportPermissionDenied() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastEmitElapsed < COOLDOWN_MS) return
        lastEmitElapsed = now
        _permissionDenied.tryEmit(Unit)
    }

    private companion object {
        const val COOLDOWN_MS = 5_000L
    }
}
