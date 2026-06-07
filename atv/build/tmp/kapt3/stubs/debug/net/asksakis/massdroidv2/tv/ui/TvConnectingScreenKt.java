package net.asksakis.massdroidv2.tv.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u00a8\u0006\u0006"}, d2 = {"TvConnectingScreen", "", "server", "", "onChangeServer", "Lkotlin/Function0;", "atv_debug"})
public final class TvConnectingScreenKt {
    
    /**
     * Shown while auto-connecting to the saved server. Keeps the user here (with a
     * retry running in the VM) on transient failures instead of forcing re-login;
     * a button lets them switch servers if needed.
     */
    @androidx.compose.runtime.Composable()
    public static final void TvConnectingScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String server, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onChangeServer) {
    }
}