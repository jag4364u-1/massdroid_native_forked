package net.asksakis.massdroidv2.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import net.asksakis.massdroidv2.data.util.ProviderHealthReporter
import javax.inject.Inject

/**
 * Exposes app-level, view-agnostic notices to the single global Snackbar host in [MassDroidApp].
 * Currently surfaces [ProviderHealthReporter.searchDegraded] (bulk MA resolution timed out because
 * a music provider is slow/rate-limited), so no individual screen needs its own failure plumbing.
 */
@HiltViewModel
class AppNoticesViewModel @Inject constructor(
    reporter: ProviderHealthReporter
) : ViewModel() {
    val searchDegraded: SharedFlow<Unit> = reporter.searchDegraded
}
