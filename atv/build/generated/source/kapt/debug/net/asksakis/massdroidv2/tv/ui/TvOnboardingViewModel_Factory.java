package net.asksakis.massdroidv2.tv.ui;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
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
public final class TvOnboardingViewModel_Factory implements Factory<TvOnboardingViewModel> {
  private final Provider<MaWebSocketClient> wsClientProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public TvOnboardingViewModel_Factory(Provider<MaWebSocketClient> wsClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.wsClientProvider = wsClientProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public TvOnboardingViewModel get() {
    return newInstance(wsClientProvider.get(), settingsRepositoryProvider.get());
  }

  public static TvOnboardingViewModel_Factory create(Provider<MaWebSocketClient> wsClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new TvOnboardingViewModel_Factory(wsClientProvider, settingsRepositoryProvider);
  }

  public static TvOnboardingViewModel newInstance(MaWebSocketClient wsClient,
      SettingsRepository settingsRepository) {
    return new TvOnboardingViewModel(wsClient, settingsRepository);
  }
}
