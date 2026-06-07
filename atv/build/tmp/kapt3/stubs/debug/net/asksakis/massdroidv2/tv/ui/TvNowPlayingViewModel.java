package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import net.asksakis.massdroidv2.domain.model.Player;
import net.asksakis.massdroidv2.domain.model.QueueState;
import net.asksakis.massdroidv2.domain.model.RepeatMode;
import net.asksakis.massdroidv2.domain.repository.MusicRepository;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import javax.inject.Inject;

/**
 * Controls a single MA player from the TV: transport + volume, targeting the
 * player id passed via navigation. Selecting it also makes it the app's current
 * player so library plays from the home land here.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\b\u0007\u0018\u0000 \'2\u00020\u0001:\u0001\'B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0002J\u0006\u0010\u001e\u001a\u00020\u001bJ\u0006\u0010\u001f\u001a\u00020\u0019J\u0006\u0010 \u001a\u00020\u0019J\u0006\u0010!\u001a\u00020\u0019J\u000e\u0010\"\u001a\u00020\u001b2\u0006\u0010#\u001a\u00020\u000bJ\u0006\u0010$\u001a\u00020\u001bJ\u0006\u0010%\u001a\u00020\u001bJ\u0006\u0010&\u001a\u00020\u001bR\u0016\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00110\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000fR\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvNowPlayingViewModel;", "Landroidx/lifecycle/ViewModel;", "playerRepository", "Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;", "musicRepository", "Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;", "savedStateHandle", "Landroidx/lifecycle/SavedStateHandle;", "(Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;Landroidx/lifecycle/SavedStateHandle;)V", "_seekTarget", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "elapsed", "Lkotlinx/coroutines/flow/StateFlow;", "getElapsed", "()Lkotlinx/coroutines/flow/StateFlow;", "player", "Lnet/asksakis/massdroidv2/domain/model/Player;", "getPlayer", "playerId", "", "queueState", "Lnet/asksakis/massdroidv2/domain/model/QueueState;", "getQueueState", "seekJob", "Lkotlinx/coroutines/Job;", "changeVolume", "", "delta", "", "cycleRepeat", "next", "playPause", "previous", "seekBy", "deltaSec", "toggleShuffle", "volumeDown", "volumeUp", "Companion", "atv_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TvNowPlayingViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String playerId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.domain.model.Player> player = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Double> _seekTarget = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job seekJob;
    
    /**
     * Live playback position (seconds): the optimistic target if seeking, else the :core ticker.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> elapsed = null;
    
    /**
     * Selected queue state (shuffle/repeat live here, per MA).
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.domain.model.QueueState> queueState = null;
    @java.lang.Deprecated()
    public static final int VOLUME_STEP = 5;
    @java.lang.Deprecated()
    public static final long SEEK_DEBOUNCE_MS = 350L;
    @java.lang.Deprecated()
    public static final double SEEK_RECONCILE_TOLERANCE_S = 1.5;
    @java.lang.Deprecated()
    public static final long SEEK_OVERRIDE_MAX_MS = 4000L;
    @org.jetbrains.annotations.NotNull()
    private static final net.asksakis.massdroidv2.tv.ui.TvNowPlayingViewModel.Companion Companion = null;
    
    @javax.inject.Inject()
    public TvNowPlayingViewModel(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.SavedStateHandle savedStateHandle) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.domain.model.Player> getPlayer() {
        return null;
    }
    
    /**
     * Live playback position (seconds): the optimistic target if seeking, else the :core ticker.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getElapsed() {
        return null;
    }
    
    /**
     * Selected queue state (shuffle/repeat live here, per MA).
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.domain.model.QueueState> getQueueState() {
        return null;
    }
    
    public final void toggleShuffle() {
    }
    
    public final void cycleRepeat() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job playPause() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job next() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job previous() {
        return null;
    }
    
    /**
     * Seek by a relative delta (seconds), clamped to the track.
     */
    public final void seekBy(double deltaSec) {
    }
    
    public final void volumeUp() {
    }
    
    public final void volumeDown() {
    }
    
    private final void changeVolume(int delta) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvNowPlayingViewModel$Companion;", "", "()V", "SEEK_DEBOUNCE_MS", "", "SEEK_OVERRIDE_MAX_MS", "SEEK_RECONCILE_TOLERANCE_S", "", "VOLUME_STEP", "", "atv_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}