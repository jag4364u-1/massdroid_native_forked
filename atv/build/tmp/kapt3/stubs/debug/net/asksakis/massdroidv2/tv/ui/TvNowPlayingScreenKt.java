package net.asksakis.massdroidv2.tv.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.input.key.KeyEventType;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.focus.FocusRequester;
import androidx.tv.material3.IconButtonDefaults;
import net.asksakis.massdroidv2.domain.model.PlaybackState;
import net.asksakis.massdroidv2.domain.model.RepeatMode;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a4\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00030\tH\u0003\u001a:\u0010\n\u001a\u00020\u00032\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00072\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00030\u0013H\u0003\u001a,\u0010\u0014\u001a\u00020\u00032\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\u00132\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0007\u001a\u0010\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u0001H\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"SEEK_STEP_S", "", "SeekBar", "", "elapsed", "duration", "enabled", "", "onSeekBy", "Lkotlin/Function1;", "TransportIcon", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "description", "", "modifier", "Landroidx/compose/ui/Modifier;", "active", "onClick", "Lkotlin/Function0;", "TvNowPlayingScreen", "onOpenQueue", "viewModel", "Lnet/asksakis/massdroidv2/tv/ui/TvNowPlayingViewModel;", "settingsViewModel", "Lnet/asksakis/massdroidv2/tv/ui/TvSettingsViewModel;", "formatTime", "seconds", "atv_debug"})
public final class TvNowPlayingScreenKt {
    private static final double SEEK_STEP_S = 10.0;
    
    @androidx.compose.runtime.Composable()
    public static final void TvNowPlayingScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenQueue, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.tv.ui.TvNowPlayingViewModel viewModel, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.tv.ui.TvSettingsViewModel settingsViewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TransportIcon(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String description, androidx.compose.ui.Modifier modifier, boolean active, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SeekBar(double elapsed, double duration, boolean enabled, kotlin.jvm.functions.Function1<? super java.lang.Double, kotlin.Unit> onSeekBy) {
    }
    
    private static final java.lang.String formatTime(double seconds) {
        return null;
    }
}