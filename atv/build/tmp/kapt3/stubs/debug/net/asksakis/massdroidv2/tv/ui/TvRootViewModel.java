package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import net.asksakis.massdroidv2.data.websocket.ConnectionState;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import javax.inject.Inject;

/**
 * Decides the top-level TV destination and owns connection lifecycle.
 *
 * With a saved server it auto-connects and KEEPS RETRYING on transient failures
 * (e.g. a local/split-horizon hostname that briefly fails to resolve on the TV
 * right after wake), showing a "connecting" state instead of dumping the user
 * back to the login form. The login form only appears when there is no saved
 * server, or when the user explicitly chooses to change it.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0019\u001a\u00020\u001aJ\u0006\u0010\u001b\u001a\u00020\u0018R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000fR\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000fR\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvRootViewModel;", "Landroidx/lifecycle/ViewModel;", "wsClient", "Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;", "settingsRepository", "Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "(Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;)V", "_changeServerRequested", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_hasSavedServer", "_initialized", "changeServerRequested", "Lkotlinx/coroutines/flow/StateFlow;", "getChangeServerRequested", "()Lkotlinx/coroutines/flow/StateFlow;", "connectionState", "Lnet/asksakis/massdroidv2/data/websocket/ConnectionState;", "getConnectionState", "hasSavedServer", "getHasSavedServer", "initialized", "getInitialized", "savedUrl", "", "changeServer", "", "serverLabel", "Companion", "atv_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TvRootViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.data.websocket.MaWebSocketClient wsClient = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.data.websocket.ConnectionState> connectionState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _hasSavedServer = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> hasSavedServer = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _changeServerRequested = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> changeServerRequested = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _initialized = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> initialized = null;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String savedUrl = "";
    @java.lang.Deprecated()
    public static final long RETRY_DELAY_MS = 3000L;
    @org.jetbrains.annotations.NotNull()
    private static final net.asksakis.massdroidv2.tv.ui.TvRootViewModel.Companion Companion = null;
    
    @javax.inject.Inject()
    public TvRootViewModel(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.data.websocket.MaWebSocketClient wsClient, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.data.websocket.ConnectionState> getConnectionState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getHasSavedServer() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getChangeServerRequested() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getInitialized() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String serverLabel() {
        return null;
    }
    
    /**
     * User chose to enter a different server: stop retrying and show onboarding.
     */
    public final void changeServer() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvRootViewModel$Companion;", "", "()V", "RETRY_DELAY_MS", "", "atv_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}