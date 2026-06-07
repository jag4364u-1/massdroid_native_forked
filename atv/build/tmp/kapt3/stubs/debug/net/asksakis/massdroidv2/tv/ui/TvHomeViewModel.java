package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import net.asksakis.massdroidv2.domain.model.Album;
import net.asksakis.massdroidv2.domain.model.Artist;
import net.asksakis.massdroidv2.domain.model.Player;
import net.asksakis.massdroidv2.domain.model.Playlist;
import net.asksakis.massdroidv2.domain.repository.MusicRepository;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import javax.inject.Inject;

/**
 * Connected home content, ATV style: live players plus library shelves pulled
 * straight from the shared :core repositories and rendered as D-pad card rows.
 *
 * Albums and Artists are paginated so the whole library is reachable: each row
 * fetches the next page as you scroll near its end, with no fixed cap. Recently
 * Played and Playlists stay as bounded previews.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001:\u00014B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJK\u0010%\u001a\u00020&\"\u0004\b\u0000\u0010\'2\u0012\u0010(\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\'0\u000b0\n2\"\u0010)\u001a\u001e\b\u0001\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\'0\u000b0+\u0012\u0006\u0012\u0004\u0018\u00010,0*H\u0002\u00a2\u0006\u0002\u0010-J\u0006\u0010.\u001a\u00020&J\u0006\u0010/\u001a\u00020&J\u000e\u00100\u001a\u00020&2\u0006\u00101\u001a\u00020\u001aJ\u000e\u00102\u001a\u00020&2\u0006\u00103\u001a\u00020\u001aR\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00160\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0019\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012R\u001d\u0010\u001f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0012R\u001d\u0010!\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0012R\u0019\u0010#\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001a0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0012\u00a8\u00065"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvHomeViewModel;", "Landroidx/lifecycle/ViewModel;", "musicRepository", "Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;", "playerRepository", "Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;", "settingsRepository", "Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "(Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;)V", "_playlists", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lnet/asksakis/massdroidv2/domain/model/Playlist;", "_recentlyPlayed", "Lnet/asksakis/massdroidv2/domain/model/Album;", "albums", "Lkotlinx/coroutines/flow/StateFlow;", "getAlbums", "()Lkotlinx/coroutines/flow/StateFlow;", "albumsPager", "Lnet/asksakis/massdroidv2/tv/ui/TvHomeViewModel$Pager;", "artists", "Lnet/asksakis/massdroidv2/domain/model/Artist;", "getArtists", "artistsPager", "localPlayerId", "", "getLocalPlayerId", "players", "Lnet/asksakis/massdroidv2/domain/model/Player;", "getPlayers", "playlists", "getPlaylists", "recentlyPlayed", "getRecentlyPlayed", "selectedPlayerId", "getSelectedPlayerId", "load", "", "T", "target", "block", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "", "(Lkotlinx/coroutines/flow/MutableStateFlow;Lkotlin/jvm/functions/Function1;)V", "loadMoreAlbums", "loadMoreArtists", "playMedia", "uri", "selectPlayer", "playerId", "Pager", "atv_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TvHomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Player>> players = null;
    
    /**
     * MA player id of this device's own Sendspin player (the local "MassDroid TV").
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> localPlayerId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> selectedPlayerId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> _recentlyPlayed = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> recentlyPlayed = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.tv.ui.TvHomeViewModel.Pager<net.asksakis.massdroidv2.domain.model.Album> albumsPager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> albums = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.tv.ui.TvHomeViewModel.Pager<net.asksakis.massdroidv2.domain.model.Artist> artistsPager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Artist>> artists = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Playlist>> _playlists = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Playlist>> playlists = null;
    
    @javax.inject.Inject()
    public TvHomeViewModel(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Player>> getPlayers() {
        return null;
    }
    
    /**
     * MA player id of this device's own Sendspin player (the local "MassDroid TV").
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getLocalPlayerId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSelectedPlayerId() {
        return null;
    }
    
    public final void selectPlayer(@org.jetbrains.annotations.NotNull()
    java.lang.String playerId) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> getRecentlyPlayed() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> getAlbums() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Artist>> getArtists() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Playlist>> getPlaylists() {
        return null;
    }
    
    /**
     * Fetch the next page of albums when the row nears its end.
     */
    public final void loadMoreAlbums() {
    }
    
    /**
     * Fetch the next page of artists when the row nears its end.
     */
    public final void loadMoreArtists() {
    }
    
    /**
     * Play a library item on the selected player (or the first available one).
     */
    public final void playMedia(@org.jetbrains.annotations.NotNull()
    java.lang.String uri) {
    }
    
    private final <T extends java.lang.Object>void load(kotlinx.coroutines.flow.MutableStateFlow<java.util.List<T>> target, kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super java.util.List<? extends T>>, ? extends java.lang.Object> block) {
    }
    
    /**
     * Offset-based forward pager over a :core library endpoint. De-duplicates by
     * [key] when appending so the LazyRow never sees a duplicate stable key, and
     * stops once a short/empty page signals the end.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0002\u0018\u0000 \u001d*\u0004\b\u0000\u0010\u00012\u00020\u0002:\u0001\u001dBo\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00020\u0006\u0012L\u0010\u0007\u001aH\b\u0001\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\r\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000f0\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00020\b\u00a2\u0006\u0002\u0010\u0010J\u0006\u0010\u001b\u001a\u00020\u001cR\u001a\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000f0\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000RV\u0010\u0007\u001aH\b\u0001\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0013\u0012\u00110\t\u00a2\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\r\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000f0\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00020\bX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0015R\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u000f0\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u001a\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001e"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvHomeViewModel$Pager;", "T", "", "scope", "Lkotlinx/coroutines/CoroutineScope;", "key", "Lkotlin/Function1;", "fetch", "Lkotlin/Function3;", "", "Lkotlin/ParameterName;", "name", "limit", "offset", "Lkotlin/coroutines/Continuation;", "", "(Lkotlinx/coroutines/CoroutineScope;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function3;)V", "_items", "Lkotlinx/coroutines/flow/MutableStateFlow;", "endReached", "", "Lkotlin/jvm/functions/Function3;", "items", "Lkotlinx/coroutines/flow/StateFlow;", "getItems", "()Lkotlinx/coroutines/flow/StateFlow;", "loading", "loadMore", "", "Companion", "atv_debug"})
    static final class Pager<T extends java.lang.Object> {
        @org.jetbrains.annotations.NotNull()
        private final kotlinx.coroutines.CoroutineScope scope = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<T, java.lang.Object> key = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function3<java.lang.Integer, java.lang.Integer, kotlin.coroutines.Continuation<? super java.util.List<? extends T>>, java.lang.Object> fetch = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<T>> _items = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlinx.coroutines.flow.StateFlow<java.util.List<T>> items = null;
        private int offset = 0;
        private boolean endReached = false;
        private boolean loading = false;
        @java.lang.Deprecated()
        public static final int PAGE_SIZE = 50;
        @org.jetbrains.annotations.NotNull()
        private static final net.asksakis.massdroidv2.tv.ui.TvHomeViewModel.Pager.Companion Companion = null;
        
        public Pager(@org.jetbrains.annotations.NotNull()
        kotlinx.coroutines.CoroutineScope scope, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super T, ? extends java.lang.Object> key, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.lang.Integer, ? super kotlin.coroutines.Continuation<? super java.util.List<? extends T>>, ? extends java.lang.Object> fetch) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlinx.coroutines.flow.StateFlow<java.util.List<T>> getItems() {
            return null;
        }
        
        public final void loadMore() {
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvHomeViewModel$Pager$Companion;", "", "()V", "PAGE_SIZE", "", "atv_debug"})
        static final class Companion {
            
            private Companion() {
                super();
            }
        }
    }
}