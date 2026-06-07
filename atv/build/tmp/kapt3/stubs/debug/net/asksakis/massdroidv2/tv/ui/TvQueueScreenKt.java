package net.asksakis.massdroidv2.tv.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.material.icons.Icons;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000,\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a8\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00062\b\u0010\b\u001a\u0004\u0018\u00010\u00062\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\fH\u0003\u001a\u0012\u0010\r\u001a\u00020\u00042\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0007\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u00a8\u0006\u0010"}, d2 = {"EDGE", "Landroidx/compose/ui/unit/Dp;", "F", "QueueRow", "", "title", "", "subtitle", "imageUrl", "isCurrent", "", "onClick", "Lkotlin/Function0;", "TvQueueScreen", "viewModel", "Lnet/asksakis/massdroidv2/tv/ui/TvQueueViewModel;", "atv_debug"})
public final class TvQueueScreenKt {
    
    /**
     * Overscan-safe horizontal inset for 10-foot layout.
     */
    private static final float EDGE = 0.0F;
    
    /**
     * 10-foot view of the active queue. Current track highlighted; click plays that index.
     */
    @androidx.compose.runtime.Composable()
    public static final void TvQueueScreen(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.tv.ui.TvQueueViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QueueRow(java.lang.String title, java.lang.String subtitle, java.lang.String imageUrl, boolean isCurrent, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}