package net.asksakis.massdroidv2.tv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.core.app.NotificationCompat;
import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.Dispatchers;
import net.asksakis.massdroidv2.data.sendspin.SendspinManager;
import net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher;
import net.asksakis.massdroidv2.playback.SendspinCoordinator;
import net.asksakis.massdroidv2.playback.SendspinMetadata;
import net.asksakis.massdroidv2.tv.ui.TvMainActivity;
import javax.inject.Inject;

/**
 * Foreground service that makes the Shield itself a Sendspin player: it drives
 * the shared :core [SendspinCoordinator] (which owns the Sendspin engine + Oboe
 * output) WITHOUT the phone-only Android Auto / Follow Me wiring.
 *
 * It also publishes a [MediaSessionCompat] with live metadata + playback state
 * and a MediaStyle notification, so the Android TV home and system controls show
 * a "now playing" surface for this device, like any other music player. Transport
 * commands from that surface route back into the Sendspin controller.
 */
@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u008a\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0006\b\u0007\u0018\u0000 J2\u00020\u0001:\u0001JB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u00105\u001a\u000206H\u0002J\b\u00107\u001a\u000208H\u0002J\b\u00109\u001a\u000208H\u0002J\u0014\u0010:\u001a\u0004\u0018\u00010;2\b\u0010<\u001a\u0004\u0018\u00010=H\u0016J\b\u0010>\u001a\u000208H\u0016J\b\u0010?\u001a\u000208H\u0016J\u0010\u0010@\u001a\u0002082\u0006\u0010A\u001a\u00020\bH\u0002J\u0010\u0010B\u001a\u0002082\u0006\u0010C\u001a\u00020\u0006H\u0002J\"\u0010D\u001a\u00020E2\b\u0010<\u001a\u0004\u0018\u00010=2\u0006\u0010F\u001a\u00020E2\u0006\u0010G\u001a\u00020EH\u0016J\b\u0010H\u001a\u000208H\u0002J\b\u0010I\u001a\u000208H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u000f\u001a\u00020\u00108\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0017\u001a\u00020\u00188\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001e\u0010\u001d\u001a\u00020\u001e8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001e\u0010#\u001a\u00020$8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b\'\u0010(R\u001e\u0010)\u001a\u00020*8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001e\u0010/\u001a\u0002008\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b1\u00102\"\u0004\b3\u00104\u00a8\u0006K"}, d2 = {"Lnet/asksakis/massdroidv2/tv/TvPlaybackService;", "Landroid/app/Service;", "()V", "coordinator", "Lnet/asksakis/massdroidv2/playback/SendspinCoordinator;", "isPlaying", "", "lastMetadata", "Lnet/asksakis/massdroidv2/playback/SendspinMetadata;", "mainHandler", "Landroid/os/Handler;", "mediaCallback", "Landroid/support/v4/media/session/MediaSessionCompat$Callback;", "mediaSession", "Landroid/support/v4/media/session/MediaSessionCompat;", "playerRepository", "Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;", "getPlayerRepository", "()Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;", "setPlayerRepository", "(Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;)V", "scope", "Lkotlinx/coroutines/CoroutineScope;", "sendspinManager", "Lnet/asksakis/massdroidv2/data/sendspin/SendspinManager;", "getSendspinManager", "()Lnet/asksakis/massdroidv2/data/sendspin/SendspinManager;", "setSendspinManager", "(Lnet/asksakis/massdroidv2/data/sendspin/SendspinManager;)V", "sendspinVolumeCoordinator", "Lnet/asksakis/massdroidv2/data/sendspin/SendspinVolumeCoordinator;", "getSendspinVolumeCoordinator", "()Lnet/asksakis/massdroidv2/data/sendspin/SendspinVolumeCoordinator;", "setSendspinVolumeCoordinator", "(Lnet/asksakis/massdroidv2/data/sendspin/SendspinVolumeCoordinator;)V", "settingsRepository", "Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "getSettingsRepository", "()Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "setSettingsRepository", "(Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;)V", "shortcutDispatcher", "Lnet/asksakis/massdroidv2/domain/shortcut/ShortcutActionDispatcher;", "getShortcutDispatcher", "()Lnet/asksakis/massdroidv2/domain/shortcut/ShortcutActionDispatcher;", "setShortcutDispatcher", "(Lnet/asksakis/massdroidv2/domain/shortcut/ShortcutActionDispatcher;)V", "wsClient", "Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;", "getWsClient", "()Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;", "setWsClient", "(Lnet/asksakis/massdroidv2/data/websocket/MaWebSocketClient;)V", "buildNotification", "Landroid/app/Notification;", "createChannel", "", "defaultPlayerIcon", "onBind", "Landroid/os/IBinder;", "intent", "Landroid/content/Intent;", "onCreate", "onDestroy", "onMetadata", "meta", "onPlayingChanged", "playing", "onStartCommand", "", "flags", "startId", "startForegroundCompat", "updateNotification", "Companion", "atv_debug"})
public final class TvPlaybackService extends android.app.Service {
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.data.sendspin.SendspinManager sendspinManager;
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator sendspinVolumeCoordinator;
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository;
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository;
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.data.websocket.MaWebSocketClient wsClient;
    @javax.inject.Inject()
    public net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher shortcutDispatcher;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler mainHandler = null;
    private net.asksakis.massdroidv2.playback.SendspinCoordinator coordinator;
    private android.support.v4.media.session.MediaSessionCompat mediaSession;
    @org.jetbrains.annotations.Nullable()
    private net.asksakis.massdroidv2.playback.SendspinMetadata lastMetadata;
    private boolean isPlaying = false;
    @org.jetbrains.annotations.NotNull()
    private final android.support.v4.media.session.MediaSessionCompat.Callback mediaCallback = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String CHANNEL_ID = "tv_playback";
    private static final int NOTIFICATION_ID = 1;
    private static final int ICON_WAIT_TICKS = 30;
    @org.jetbrains.annotations.NotNull()
    public static final net.asksakis.massdroidv2.tv.TvPlaybackService.Companion Companion = null;
    
    public TvPlaybackService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.data.sendspin.SendspinManager getSendspinManager() {
        return null;
    }
    
    public final void setSendspinManager(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.data.sendspin.SendspinManager p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator getSendspinVolumeCoordinator() {
        return null;
    }
    
    public final void setSendspinVolumeCoordinator(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.domain.repository.SettingsRepository getSettingsRepository() {
        return null;
    }
    
    public final void setSettingsRepository(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.SettingsRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.domain.repository.PlayerRepository getPlayerRepository() {
        return null;
    }
    
    public final void setPlayerRepository(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.PlayerRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.data.websocket.MaWebSocketClient getWsClient() {
        return null;
    }
    
    public final void setWsClient(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.data.websocket.MaWebSocketClient p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher getShortcutDispatcher() {
        return null;
    }
    
    public final void setShortcutDispatcher(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher p0) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    private final void onMetadata(net.asksakis.massdroidv2.playback.SendspinMetadata meta) {
    }
    
    private final void onPlayingChanged(boolean playing) {
    }
    
    /**
     * Default this device's MA player icon to a television glyph once it has
     * registered, so the Shield shows as a TV (not a generic speaker) in Music
     * Assistant. MA persists it as the per-player CONF_ENTRY_PLAYER_ICON.
     */
    private final void defaultPlayerIcon() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
        return null;
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    private final void startForegroundCompat() {
    }
    
    private final void updateNotification() {
    }
    
    private final android.app.Notification buildNotification() {
        return null;
    }
    
    private final void createChannel() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lnet/asksakis/massdroidv2/tv/TvPlaybackService$Companion;", "", "()V", "CHANNEL_ID", "", "ICON_WAIT_TICKS", "", "NOTIFICATION_ID", "start", "", "context", "Landroid/content/Context;", "atv_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        public final void start(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
    }
}