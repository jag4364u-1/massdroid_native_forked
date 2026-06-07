package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import net.asksakis.massdroidv2.domain.model.QueueItem;
import net.asksakis.massdroidv2.domain.repository.MusicRepository;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import javax.inject.Inject;

/**
 * Read + play view of the selected player's active queue. Items come from the
 * shared :core [PlayerRepository.queueItems] snapshot (one RPC per queue change,
 * shared with every other consumer); tapping a row plays that index.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013R\u0019\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvQueueViewModel;", "Landroidx/lifecycle/ViewModel;", "playerRepository", "Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;", "musicRepository", "Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;", "(Lnet/asksakis/massdroidv2/domain/repository/PlayerRepository;Lnet/asksakis/massdroidv2/domain/repository/MusicRepository;)V", "currentItemId", "Lkotlinx/coroutines/flow/StateFlow;", "", "getCurrentItemId", "()Lkotlinx/coroutines/flow/StateFlow;", "items", "", "Lnet/asksakis/massdroidv2/domain/model/QueueItem;", "getItems", "playIndex", "", "index", "", "atv_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TvQueueViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.QueueItem>> items = null;
    
    /**
     * queueItemId of the currently playing item, for highlighting.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentItemId = null;
    
    @javax.inject.Inject()
    public TvQueueViewModel(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.PlayerRepository playerRepository, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.MusicRepository musicRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<net.asksakis.massdroidv2.domain.model.QueueItem>> getItems() {
        return null;
    }
    
    /**
     * queueItemId of the currently playing item, for highlighting.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentItemId() {
        return null;
    }
    
    public final void playIndex(int index) {
    }
}