package net.asksakis.massdroidv2.tv;

import android.app.Application;
import android.security.KeyChain;
import android.util.Log;
import coil.ImageLoaderFactory;
import dagger.hilt.android.HiltAndroidApp;
import kotlinx.coroutines.Dispatchers;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import javax.inject.Inject;

/**
 * Android TV (Shield) application entry point. Shares all non-UI logic with the
 * phone app via :core (data, domain, Sendspin engine + coordination, native).
 * The phone-specific bootstrap (PlaybackService, Android Auto, Follow Me) is
 * intentionally NOT here; the TV adds its own foreground Sendspin player service.
 */
@dagger.hilt.android.HiltAndroidApp()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00162\u00020\u00012\u00020\u0002:\u0001\u0016B\u0005\u00a2\u0006\u0002\u0010\u0003J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0015H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\f\u001a\u00020\r8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0017"}, d2 = {"Lnet/asksakis/massdroidv2/tv/TvApp;", "Landroid/app/Application;", "Lcoil/ImageLoaderFactory;", "()V", "appScope", "Lkotlinx/coroutines/CoroutineScope;", "settingsRepository", "Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "getSettingsRepository", "()Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "setSettingsRepository", "(Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;)V", "wsClient", "Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;", "getWsClient", "()Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;", "setWsClient", "(Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;)V", "newImageLoader", "Lcoil/ImageLoader;", "onCreate", "", "Companion", "atv_debug"})
public final class TvApp extends android.app.Application implements coil.ImageLoaderFactory {
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.data.websocket.MaWebSocketClient wsClient;
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope appScope = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String TAG = "TvApp";
    @org.jetbrains.annotations.NotNull()
    private static final net.asksakis.massdroidv2.tv.TvApp.Companion Companion = null;
    
    public TvApp() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.data.websocket.MaWebSocketClient getWsClient() {
        return null;
    }
    
    public final void setWsClient(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.data.websocket.MaWebSocketClient p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.domain.repository.SettingsRepository getSettingsRepository() {
        return null;
    }
    
    public final void setSettingsRepository(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.SettingsRepository p0) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    /**
     * Load artwork through the same authenticated/mTLS-aware OkHttp client as
     * the WS connection, mirroring the phone app.
     */
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public coil.ImageLoader newImageLoader() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lnet/asksakis/massdroidv2/tv/TvApp$Companion;", "", "()V", "TAG", "", "atv_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}