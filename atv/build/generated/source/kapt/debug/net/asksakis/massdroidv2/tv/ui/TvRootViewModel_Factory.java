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
public final class TvRootViewModel_Factory implements Factory<TvRootViewModel> {
  private final Provider<MaWebSocketClient> wsClientProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public TvRootViewModel_Factory(Provider<MaWebSocketClient> wsClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.wsClientProvider = wsClientProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public TvRootViewModel get() {
    return newInstance(wsClientProvider.get(), settingsRepositoryProvider.get());
  }

  public static TvRootViewModel_Factory create(Provider<MaWebSocketClient> wsClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new TvRootViewModel_Factory(wsClientProvider, settingsRepositoryProvider);
  }

  public static TvRootViewModel newInstance(MaWebSocketClient wsClient,
      SettingsRepository settingsRepository) {
    return new TvRootViewModel(wsClient, settingsRepository);
  }
}
