package net.asksakis.massdroidv2.tv.ui;

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
public final class TvQueueViewModel_Factory implements Factory<TvQueueViewModel> {
  private final Provider<PlayerRepository> playerRepositoryProvider;

  private final Provider<MusicRepository> musicRepositoryProvider;

  public TvQueueViewModel_Factory(Provider<PlayerRepository> playerRepositoryProvider,
      Provider<MusicRepository> musicRepositoryProvider) {
    this.playerRepositoryProvider = playerRepositoryProvider;
    this.musicRepositoryProvider = musicRepositoryProvider;
  }

  @Override
  public TvQueueViewModel get() {
    return newInstance(playerRepositoryProvider.get(), musicRepositoryProvider.get());
  }

  public static TvQueueViewModel_Factory create(Provider<PlayerRepository> playerRepositoryProvider,
      Provider<MusicRepository> musicRepositoryProvider) {
    return new TvQueueViewModel_Factory(playerRepositoryProvider, musicRepositoryProvider);
  }

  public static TvQueueViewModel newInstance(PlayerRepository playerRepository,
      MusicRepository musicRepository) {
    return new TvQueueViewModel(playerRepository, musicRepository);
  }
}
