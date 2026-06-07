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
public final class TvNowPlayingViewModel_Factory implements Factory<TvNowPlayingViewModel> {
  private final Provider<PlayerRepository> playerRepositoryProvider;

  private final Provider<MusicRepository> musicRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public TvNowPlayingViewModel_Factory(Provider<PlayerRepository> playerRepositoryProvider,
      Provider<MusicRepository> musicRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.playerRepositoryProvider = playerRepositoryProvider;
    this.musicRepositoryProvider = musicRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public TvNowPlayingViewModel get() {
    return newInstance(playerRepositoryProvider.get(), musicRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static TvNowPlayingViewModel_Factory create(
      Provider<PlayerRepository> playerRepositoryProvider,
      Provider<MusicRepository> musicRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new TvNowPlayingViewModel_Factory(playerRepositoryProvider, musicRepositoryProvider, savedStateHandleProvider);
  }

  public static TvNowPlayingViewModel newInstance(PlayerRepository playerRepository,
      MusicRepository musicRepository, SavedStateHandle savedStateHandle) {
    return new TvNowPlayingViewModel(playerRepository, musicRepository, savedStateHandle);
  }
}
