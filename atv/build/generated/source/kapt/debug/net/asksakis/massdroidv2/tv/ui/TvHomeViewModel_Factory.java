package net.asksakis.massdroidv2.tv.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.asksakis.massdroidv2.domain.repository.MusicRepository;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;

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
public final class TvHomeViewModel_Factory implements Factory<TvHomeViewModel> {
  private final Provider<MusicRepository> musicRepositoryProvider;

  private final Provider<PlayerRepository> playerRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public TvHomeViewModel_Factory(Provider<MusicRepository> musicRepositoryProvider,
      Provider<PlayerRepository> playerRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.musicRepositoryProvider = musicRepositoryProvider;
    this.playerRepositoryProvider = playerRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public TvHomeViewModel get() {
    return newInstance(musicRepositoryProvider.get(), playerRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static TvHomeViewModel_Factory create(Provider<MusicRepository> musicRepositoryProvider,
      Provider<PlayerRepository> playerRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new TvHomeViewModel_Factory(musicRepositoryProvider, playerRepositoryProvider, settingsRepositoryProvider);
  }

  public static TvHomeViewModel newInstance(MusicRepository musicRepository,
      PlayerRepository playerRepository, SettingsRepository settingsRepository) {
    return new TvHomeViewModel(musicRepository, playerRepository, settingsRepository);
  }
}
