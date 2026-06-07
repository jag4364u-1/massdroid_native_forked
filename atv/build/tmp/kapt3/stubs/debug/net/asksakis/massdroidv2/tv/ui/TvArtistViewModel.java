package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import net.asksakis.massdroidv2.domain.model.Album;
import net.asksakis.massdroidv2.domain.repository.MusicRepository;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import javax.inject.Inject;

/**
 * One artist's albums, so the user can pick which to play (artists are not played
 * directly from the home — they drill into their releases first). Plays the chosen
 * album on the currently selected player.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u000eR\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvArtistViewModel;", "Landroidx/lifecycle/ViewModel;", "musicRepository", "Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;", "playerRepository", "Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;", "savedStateHandle", "Landroidx/lifecycle/SavedStateHandle;", "(Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;Landroidx/lifecycle/SavedStateHandle;)V", "_albums", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lnet/asksakis/massdroidv2/domain/model/Album;", "_name", "", "albums", "Lkotlinx/coroutines/flow/StateFlow;", "getAlbums", "()Lkotlinx/coroutines/flow/StateFlow;", "itemId", "name", "getName", "provider", "playMedia", "", "uri", "atv_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TvArtistViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String itemId = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String provider = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _name = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> name = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> _albums = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> albums = null;
    
    @javax.inject.Inject()
    public TvArtistViewModel(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.SavedStateHandle savedStateHandle) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.Album>> getAlbums() {
        return null;
    }
    
    /**
     * Play an album on the selected player (or the first available one).
     */
    public final void playMedia(@org.jetbrains.annotations.NotNull()
    java.lang.String uri) {
    }
}