package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import net.asksakis.massdroidv2.domain.model.SendspinAudioFormat;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import javax.inject.Inject;

/**
 * TV settings. The audio sync delay (ms) is persisted to the shared settings and
 * applied live by the core SendspinAudioController (which observes it and calls
 * SendspinManager.setSyncDelayMs). Negative shifts this device's playback
 * EARLIER, to compensate HDMI/AVR output latency so the Shield aligns with the
 * rest of a sync group.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000bJ\u000e\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0007J\u000e\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u000bR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0014"}, d2 = {"Lnet/asksakis/massdroidv2/tv/ui/TvSettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "settingsRepository", "Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;", "(Lnet/asksakis/massdroidv2/domain/repository/SettingsRepository;)V", "audioFormat", "Lkotlinx/coroutines/flow/StateFlow;", "Lnet/asksakis/massdroidv2/domain/model/SendspinAudioFormat;", "getAudioFormat", "()Lkotlinx/coroutines/flow/StateFlow;", "syncDelayMs", "", "getSyncDelayMs", "nudge", "", "deltaMs", "setAudioFormat", "format", "setSyncDelay", "ms", "atv_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class TvSettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> syncDelayMs = null;
    
    /**
     * Audio quality/codec. Applied live by the core coordinator's format observer.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.domain.model.SendspinAudioFormat> audioFormat = null;
    
    @javax.inject.Inject()
    public TvSettingsViewModel(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.repository.SettingsRepository settingsRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Integer> getSyncDelayMs() {
        return null;
    }
    
    /**
     * Audio quality/codec. Applied live by the core coordinator's format observer.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<net.asksakis.massdroidv2.domain.model.SendspinAudioFormat> getAudioFormat() {
        return null;
    }
    
    public final void setSyncDelay(int ms) {
    }
    
    public final void setAudioFormat(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.domain.model.SendspinAudioFormat format) {
    }
    
    public final void nudge(int deltaMs) {
    }
}