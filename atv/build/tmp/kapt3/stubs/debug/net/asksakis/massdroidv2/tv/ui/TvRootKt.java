package net.asksakis.massdroidv2.tv.ui;

import android.net.Uri;
import androidx.compose.runtime.Composable;
import androidx.navigation.NavType;
import net.asksakis.massdroidv2.data.websocket.ConnectionState;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0007\u00a8\u0006\u0004"}, d2 = {"TvRoot", "", "viewModel", "Lnet/asksakis/massdroidv2/tv/ui/TvRootViewModel;", "atv_debug"})
public final class TvRootKt {
    
    /**
     * Top-level TV destination switch:
     * - Connected            -> navigable home / now-playing graph
     * - saved server, not yet -> "connecting" (auto-retry, no forced re-login)
     * - no saved server       -> onboarding/login
     */
    @androidx.compose.runtime.Composable()
    public static final void TvRoot(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.tv.ui.TvRootViewModel viewModel) {
    }
}