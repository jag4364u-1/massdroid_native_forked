package net.asksakis.massdroidv2.tv.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.material.icons.Icons;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.focus.FocusRequester;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.tv.material3.Border;
import androidx.tv.material3.CardDefaults;
import net.asksakis.massdroidv2.domain.model.PlaybackState;
import net.asksakis.massdroidv2.domain.model.Player;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u001aN\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00060\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\u0010\b\u0002\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0011H\u0003\u001a&\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0011H\u0003\u001a8\u0010\u0014\u001a\u00020\u00062\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u000f2\b\b\u0002\u0010\u0019\u001a\u00020\u001a2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0011H\u0003\u001a3\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\u0017\u0010\u001e\u001a\u0013\u0012\u0004\u0012\u00020\u001f\u0012\u0004\u0012\u00020\u00060\r\u00a2\u0006\u0002\b H\u0003\u001al\u0010!\u001a\u00020\u00062\u0012\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00060\r2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00060\u001126\u0010$\u001a2\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b&\u0012\b\b\'\u0012\u0004\b\b((\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b&\u0012\b\b\'\u0012\u0004\b\b()\u0012\u0004\u0012\u00020\u00060%2\b\b\u0002\u0010*\u001a\u00020+H\u0007\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"EDGE", "Landroidx/compose/ui/unit/Dp;", "F", "LOAD_MORE_THRESHOLD", "", "ContentShelf", "", "title", "", "items", "", "Lnet/asksakis/massdroidv2/tv/ui/MediaCardData;", "onClick", "Lkotlin/Function1;", "circular", "", "onLoadMore", "Lkotlin/Function0;", "MediaCard", "item", "PlayerCard", "player", "Lnet/asksakis/massdroidv2/domain/model/Player;", "selected", "local", "modifier", "Landroidx/compose/ui/Modifier;", "Shelf", "state", "Landroidx/compose/foundation/lazy/LazyListState;", "content", "Landroidx/compose/foundation/lazy/LazyListScope;", "Lkotlin/ExtensionFunctionType;", "TvHomeScreen", "onOpenPlayer", "onOpenSettings", "onOpenArtist", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "itemId", "provider", "viewModel", "Lnet/asksakis/massdroidv2/tv/ui/TvHomeViewModel;", "atv_debug"})
public final class TvHomeScreenKt {
    private static final int LOAD_MORE_THRESHOLD = 10;
    
    /**
     * Overscan-safe horizontal inset for 10-foot layout.
     */
    private static final float EDGE = 0.0F;
    
    @androidx.compose.runtime.Composable()
    public static final void TvHomeScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenPlayer, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenSettings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onOpenArtist, @org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.tv.ui.TvHomeViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ContentShelf(java.lang.String title, java.util.List<net.asksakis.massdroidv2.tv.ui.MediaCardData> items, kotlin.jvm.functions.Function1<? super net.asksakis.massdroidv2.tv.ui.MediaCardData, kotlin.Unit> onClick, boolean circular, kotlin.jvm.functions.Function0<kotlin.Unit> onLoadMore) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Shelf(java.lang.String title, androidx.compose.foundation.lazy.LazyListState state, kotlin.jvm.functions.Function1<? super androidx.compose.foundation.lazy.LazyListScope, kotlin.Unit> content) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PlayerCard(net.asksakis.massdroidv2.domain.model.Player player, boolean selected, boolean local, androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MediaCard(net.asksakis.massdroidv2.tv.ui.MediaCardData item, boolean circular, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}