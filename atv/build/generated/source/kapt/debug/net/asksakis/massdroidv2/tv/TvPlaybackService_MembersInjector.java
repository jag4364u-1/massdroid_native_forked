package net.asksakis.massdroidv2.tv;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import net.asksakis.massdroidv2.data.sendspin.SendspinManager;
import net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher;

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
public final class TvPlaybackService_MembersInjector implements MembersInjector<TvPlaybackService> {
  private final Provider<SendspinManager> sendspinManagerProvider;

  private final Provider<SendspinVolumeCoordinator> sendspinVolumeCoordinatorProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<PlayerRepository> playerRepositoryProvider;

  private final Provider<MaWebSocketClient> wsClientProvider;

  private final Provider<ShortcutActionDispatcher> shortcutDispatcherProvider;

  public TvPlaybackService_MembersInjector(Provider<SendspinManager> sendspinManagerProvider,
      Provider<SendspinVolumeCoordinator> sendspinVolumeCoordinatorProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PlayerRepository> playerRepositoryProvider,
      Provider<MaWebSocketClient> wsClientProvider,
      Provider<ShortcutActionDispatcher> shortcutDispatcherProvider) {
    this.sendspinManagerProvider = sendspinManagerProvider;
    this.sendspinVolumeCoordinatorProvider = sendspinVolumeCoordinatorProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.playerRepositoryProvider = playerRepositoryProvider;
    this.wsClientProvider = wsClientProvider;
    this.shortcutDispatcherProvider = shortcutDispatcherProvider;
  }

  public static MembersInjector<TvPlaybackService> create(
      Provider<SendspinManager> sendspinManagerProvider,
      Provider<SendspinVolumeCoordinator> sendspinVolumeCoordinatorProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PlayerRepository> playerRepositoryProvider,
      Provider<MaWebSocketClient> wsClientProvider,
      Provider<ShortcutActionDispatcher> shortcutDispatcherProvider) {
    return new TvPlaybackService_MembersInjector(sendspinManagerProvider, sendspinVolumeCoordinatorProvider, settingsRepositoryProvider, playerRepositoryProvider, wsClientProvider, shortcutDispatcherProvider);
  }

  @Override
  public void injectMembers(TvPlaybackService instance) {
    injectSendspinManager(instance, sendspinManagerProvider.get());
    injectSendspinVolumeCoordinator(instance, sendspinVolumeCoordinatorProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectPlayerRepository(instance, playerRepositoryProvider.get());
    injectWsClient(instance, wsClientProvider.get());
    injectShortcutDispatcher(instance, shortcutDispatcherProvider.get());
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvPlaybackService.sendspinManager")
  public static void injectSendspinManager(TvPlaybackService instance,
      SendspinManager sendspinManager) {
    instance.sendspinManager = sendspinManager;
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvPlaybackService.sendspinVolumeCoordinator")
  public static void injectSendspinVolumeCoordinator(TvPlaybackService instance,
      SendspinVolumeCoordinator sendspinVolumeCoordinator) {
    instance.sendspinVolumeCoordinator = sendspinVolumeCoordinator;
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvPlaybackService.settingsRepository")
  public static void injectSettingsRepository(TvPlaybackService instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvPlaybackService.playerRepository")
  public static void injectPlayerRepository(TvPlaybackService instance,
      PlayerRepository playerRepository) {
    instance.playerRepository = playerRepository;
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvPlaybackService.wsClient")
  public static void injectWsClient(TvPlaybackService instance, MaWebSocketClient wsClient) {
    instance.wsClient = wsClient;
  }

  @InjectedFieldSignature("net.asksakis.massdroidv2.tv.TvPlaybackService.shortcutDispatcher")
  public static void injectShortcutDispatcher(TvPlaybackService instance,
      ShortcutActionDispatcher shortcutDispatcher) {
    instance.shortcutDispatcher = shortcutDispatcher;
  }
}
