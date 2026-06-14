package net.asksakis.massdroidv2.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import net.asksakis.massdroidv2.data.util.AccountNoticeReporter
import net.asksakis.massdroidv2.data.util.ProviderHealthReporter
import javax.inject.Inject

/**
 * Exposes app-level, view-agnostic notices to the single global Snackbar host in [MassDroidApp].
 * Surfaces [ProviderHealthReporter.searchDegraded] (bulk MA resolution timed out because a music
 * provider is slow/rate-limited) and [AccountNoticeReporter.permissionDenied] (server rejected an
 * admin-gated command for a non-admin account, MA 2.9.0+), so no individual screen needs its own
 * failure plumbing.
 */
@HiltViewModel
class AppNoticesViewModel @Inject constructor(
    reporter: ProviderHealthReporter,
    accountNoticeReporter: AccountNoticeReporter
) : ViewModel() {
    val searchDegraded: SharedFlow<Unit> = reporter.searchDegraded
    val permissionDenied: SharedFlow<Unit> = accountNoticeReporter.permissionDenied
}
