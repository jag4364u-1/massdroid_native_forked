package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import net.asksakis.massdroidv2.data.websocket.ConnectionState;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import javax.inject.Inject;

/**
 * Onboarding: log in to a Music Assistant server with credentials, persist the
 * server URL + credentials + returned token (so the next launch auto-connects),
 * exactly mirroring the phone SettingsViewModel login flow on the shared :core
 * MaWebSocketClient.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\t2\u0006\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\tR\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0019\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvOnboardingViewModel;", "Landroidx/lifecycle/ViewModel;", "wsClient", "Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;", "settingsRepository", "Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "(Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;)V", "_error", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "connectionState", "Lkotlinx/coroutines/flow/StateFlow;", "Lnet/asksakis/massdroidv2/data/websocket/ConnectionState;", "getConnectionState", "()Lkotlinx/coroutines/flow/StateFlow;", "error", "getError", "login", "", "rawUrl", "username", "password", "atv_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TvOnboardingViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.data.websocket.MaWebSocketClient wsClient = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.data.websocket.ConnectionState> connectionState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    
    @javax.inject.Inject()
    public TvOnboardingViewModel(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.data.websocket.MaWebSocketClient wsClient, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.data.websocket.ConnectionState> getConnectionState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getError() {
        return null;
    }
    
    public final void login(@org.jetbrains.annotations.NotNull()
    java.lang.String rawUrl, @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
    }
}