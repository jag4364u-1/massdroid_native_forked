package net.asksakis.massdroidv2.tv.ui;

import androidx.lifecycle.SavedStateHandle;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.asksakis.massdroidv2.domain.repository.MusicRepository;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class TvArtistViewModel_Factory implements Factory<TvArtistViewModel> {
  private final Provider<MusicRepository> musicRepositoryProvider;

  private final Provider<PlayerRepository> playerRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public TvArtistViewModel_Factory(Provider<MusicRepository> musicRepositoryProvider,
      Provider<PlayerRepository> playerRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.musicRepositoryProvider = musicRepositoryProvider;
    this.playerRepositoryProvider = playerRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public TvArtistViewModel get() {
    return newInstance(musicRepositoryProvider.get(), playerRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static TvArtistViewModel_Factory create(Provider<MusicRepository> musicRepositoryProvider,
      Provider<PlayerRepository> playerRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new TvArtistViewModel_Factory(musicRepositoryProvider, playerRepositoryProvider, savedStateHandleProvider);
  }

  public static TvArtistViewModel newInstance(MusicRepository musicRepository,
      PlayerRepository playerRepository, SavedStateHandle savedStateHandle) {
    return new TvArtistViewModel(musicRepository, playerRepository, savedStateHandle);
  }
}
