package net.asksakis.massdroidv2.tv.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
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
public final class TvSettingsViewModel_Factory implements Factory<TvSettingsViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public TvSettingsViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public TvSettingsViewModel get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static TvSettingsViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new TvSettingsViewModel_Factory(settingsRepositoryProvider);
  }

  public static TvSettingsViewModel newInstance(SettingsRepository settingsRepository) {
    return new TvSettingsViewModel(settingsRepository);
  }
}
