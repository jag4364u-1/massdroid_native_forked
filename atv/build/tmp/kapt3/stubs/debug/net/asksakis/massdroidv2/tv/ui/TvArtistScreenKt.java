package net.asksakis.massdroidv2.tv.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.lazy.grid.GridCells;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.text.style.TextOverflow;
import net.asksakis.massdroidv2.domain.model.Album;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00040\bH\u0003\u001a\u0012\u0010\t\u001a\u00020\u00042\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0007\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u00a8\u0006\f"}, d2 = {"ARTIST_EDGE", "Landroidx/compose/ui/unit/Dp;", "F", "AlbumCard", "", "album", "Lnet/asksakis/massdroidv2/domain/model/Album;", "onClick", "Lkotlin/Function0;", "TvArtistScreen", "viewModel", "Lnet/asksakis/massdroidv2/tv/ui/TvArtistViewModel;", "atv_debug"})
public final class TvArtistScreenKt {
    private static final float ARTIST_EDGE = 0.0F;
    
    /**
     * An artist's albums in a 10-foot grid; click an album to play it on the selected player.
     */
    @androidx.compose.runtime.Composable()
    public static final void TvArtistScreen(@org.jetbrains.annotations.NotNull()
    net.asksakis.massdroidv2.tv.ui.TvArtistViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AlbumCard(net.asksakis.massdroidv2.domain.model.Album album, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
}