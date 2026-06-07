package net.asksakis.massdroidv2.tv;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;

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
public final class TvApp_MembersInjector implements MembersInjector<TvApp> {
  private final Provider<MaWebSocketClient> wsClientProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public TvApp_MembersInjector(Provider<MaWebSocketClient> wsClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.wsClientProvider = wsClientProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  public static MembersInjector<TvApp> create(Provider<MaWebSocketClient> wsClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new TvApp_MembersInjector(wsClientProvider, settingsRepositoryProvider);
  }

  @Override
  public void injectMembers(TvApp instance) {
    injectWsClient(instance, wsClientProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvApp.wsClient")
  public static void injectWsClient(TvApp instance, MaWebSocketClient wsClient) {
    instance.wsClient = wsClient;
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvApp.settingsRepository")
  public static void injectSettingsRepository(TvApp instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
