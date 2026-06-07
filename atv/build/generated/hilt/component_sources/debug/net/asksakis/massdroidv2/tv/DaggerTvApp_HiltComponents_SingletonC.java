package net.asksakis.massdroidv2.tv;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DelegateFactory;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
import net.asksakis.massdroidv2.data.database.AppDatabase;
import net.asksakis.massdroidv2.data.database.PlayHistoryDao;
import net.asksakis.massdroidv2.data.lastfm.LastFmGenreResolver;
import net.asksakis.massdroidv2.data.repository.queue.QueueItemsCoordinator;
import net.asksakis.massdroidv2.data.sendspin.SendspinClient;
import net.asksakis.massdroidv2.data.sendspin.SendspinDirectEngine;
import net.asksakis.massdroidv2.data.sendspin.SendspinManager;
import net.asksakis.massdroidv2.data.sendspin.SendspinSyncEngine;
import net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator;
import net.asksakis.massdroidv2.data.websocket.MaWebSocketClient;
import net.asksakis.massdroidv2.data.websocket.SessionEventBus;
import net.asksakis.massdroidv2.di.AppModule_ProvideAppDatabaseFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideJsonFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideMaWebSocketClientFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideOkHttpClientFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvidePlayHistoryDaoFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideSendspinClientFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideSendspinDirectEngineFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideSendspinManagerFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideSendspinSyncEngineFactory;
import net.asksakis.massdroidv2.di.AppModule_ProvideSendspinVolumeCoordinatorFactory;
import net.asksakis.massdroidv2.di.RepositoryModule_ProvideMusicRepositoryFactory;
import net.asksakis.massdroidv2.di.RepositoryModule_ProvidePlayHistoryRepositoryFactory;
import net.asksakis.massdroidv2.di.RepositoryModule_ProvidePlayerRepositoryFactory;
import net.asksakis.massdroidv2.di.RepositoryModule_ProvideSettingsRepositoryFactory;
import net.asksakis.massdroidv2.di.RepositoryModule_ProvideSmartListeningRepositoryFactory;
import net.asksakis.massdroidv2.domain.repository.MusicRepository;
import net.asksakis.massdroidv2.domain.repository.PlayHistoryRepository;
import net.asksakis.massdroidv2.domain.repository.PlayerRepository;
import net.asksakis.massdroidv2.domain.repository.SettingsRepository;
import net.asksakis.massdroidv2.domain.repository.SmartListeningRepository;
import net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher;
import net.asksakis.massdroidv2.tv.ui.TvArtistViewModel;
import net.asksakis.massdroidv2.tv.ui.TvArtistViewModel_HiltModules;
import net.asksakis.massdroidv2.tv.ui.TvHomeViewModel;
import net.asksakis.massdroidv2.tv.ui.TvHomeViewModel_HiltModules;
import net.asksakis.massdroidv2.tv.ui.TvMainActivity;
import net.asksakis.massdroidv2.tv.ui.TvNowPlayingViewModel;
import net.asksakis.massdroidv2.tv.ui.TvNowPlayingViewModel_HiltModules;
import net.asksakis.massdroidv2.tv.ui.TvOnboardingViewModel;
import net.asksakis.massdroidv2.tv.ui.TvOnboardingViewModel_HiltModules;
import net.asksakis.massdroidv2.tv.ui.TvQueueViewModel;
import net.asksakis.massdroidv2.tv.ui.TvQueueViewModel_HiltModules;
import net.asksakis.massdroidv2.tv.ui.TvRootViewModel;
import net.asksakis.massdroidv2.tv.ui.TvRootViewModel_HiltModules;
import net.asksakis.massdroidv2.tv.ui.TvSettingsViewModel;
import net.asksakis.massdroidv2.tv.ui.TvSettingsViewModel_HiltModules;
import okhttp3.OkHttpClient;

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
public final class DaggerTvApp_HiltComponents_SingletonC {
  private DaggerTvApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public TvApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements TvApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public TvApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements TvApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public TvApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements TvApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public TvApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements TvApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TvApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements TvApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TvApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements TvApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public TvApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements TvApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public TvApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends TvApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends TvApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends TvApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends TvApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(7).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvArtistViewModel, TvArtistViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvHomeViewModel, TvHomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvNowPlayingViewModel, TvNowPlayingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvOnboardingViewModel, TvOnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvQueueViewModel, TvQueueViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvRootViewModel, TvRootViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvSettingsViewModel, TvSettingsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public void injectTvMainActivity(TvMainActivity arg0) {
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String net_asksakis_massdroidv2_tv_ui_TvHomeViewModel = "net.asksakis.massdroidv2.tv.ui.TvHomeViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvArtistViewModel = "net.asksakis.massdroidv2.tv.ui.TvArtistViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvNowPlayingViewModel = "net.asksakis.massdroidv2.tv.ui.TvNowPlayingViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvOnboardingViewModel = "net.asksakis.massdroidv2.tv.ui.TvOnboardingViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvQueueViewModel = "net.asksakis.massdroidv2.tv.ui.TvQueueViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvSettingsViewModel = "net.asksakis.massdroidv2.tv.ui.TvSettingsViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvRootViewModel = "net.asksakis.massdroidv2.tv.ui.TvRootViewModel";

      @KeepFieldType
      TvHomeViewModel net_asksakis_massdroidv2_tv_ui_TvHomeViewModel2;

      @KeepFieldType
      TvArtistViewModel net_asksakis_massdroidv2_tv_ui_TvArtistViewModel2;

      @KeepFieldType
      TvNowPlayingViewModel net_asksakis_massdroidv2_tv_ui_TvNowPlayingViewModel2;

      @KeepFieldType
      TvOnboardingViewModel net_asksakis_massdroidv2_tv_ui_TvOnboardingViewModel2;

      @KeepFieldType
      TvQueueViewModel net_asksakis_massdroidv2_tv_ui_TvQueueViewModel2;

      @KeepFieldType
      TvSettingsViewModel net_asksakis_massdroidv2_tv_ui_TvSettingsViewModel2;

      @KeepFieldType
      TvRootViewModel net_asksakis_massdroidv2_tv_ui_TvRootViewModel2;
    }
  }

  private static final class ViewModelCImpl extends TvApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<TvArtistViewModel> tvArtistViewModelProvider;

    private Provider<TvHomeViewModel> tvHomeViewModelProvider;

    private Provider<TvNowPlayingViewModel> tvNowPlayingViewModelProvider;

    private Provider<TvOnboardingViewModel> tvOnboardingViewModelProvider;

    private Provider<TvQueueViewModel> tvQueueViewModelProvider;

    private Provider<TvRootViewModel> tvRootViewModelProvider;

    private Provider<TvSettingsViewModel> tvSettingsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.tvArtistViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.tvHomeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.tvNowPlayingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.tvOnboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.tvQueueViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.tvRootViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.tvSettingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(7).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvArtistViewModel, ((Provider) tvArtistViewModelProvider)).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvHomeViewModel, ((Provider) tvHomeViewModelProvider)).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvNowPlayingViewModel, ((Provider) tvNowPlayingViewModelProvider)).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvOnboardingViewModel, ((Provider) tvOnboardingViewModelProvider)).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvQueueViewModel, ((Provider) tvQueueViewModelProvider)).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvRootViewModel, ((Provider) tvRootViewModelProvider)).put(LazyClassKeyProvider.net_asksakis_massdroidv2_tv_ui_TvSettingsViewModel, ((Provider) tvSettingsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String net_asksakis_massdroidv2_tv_ui_TvNowPlayingViewModel = "net.asksakis.massdroidv2.tv.ui.TvNowPlayingViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvSettingsViewModel = "net.asksakis.massdroidv2.tv.ui.TvSettingsViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvRootViewModel = "net.asksakis.massdroidv2.tv.ui.TvRootViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvHomeViewModel = "net.asksakis.massdroidv2.tv.ui.TvHomeViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvOnboardingViewModel = "net.asksakis.massdroidv2.tv.ui.TvOnboardingViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvArtistViewModel = "net.asksakis.massdroidv2.tv.ui.TvArtistViewModel";

      static String net_asksakis_massdroidv2_tv_ui_TvQueueViewModel = "net.asksakis.massdroidv2.tv.ui.TvQueueViewModel";

      @KeepFieldType
      TvNowPlayingViewModel net_asksakis_massdroidv2_tv_ui_TvNowPlayingViewModel2;

      @KeepFieldType
      TvSettingsViewModel net_asksakis_massdroidv2_tv_ui_TvSettingsViewModel2;

      @KeepFieldType
      TvRootViewModel net_asksakis_massdroidv2_tv_ui_TvRootViewModel2;

      @KeepFieldType
      TvHomeViewModel net_asksakis_massdroidv2_tv_ui_TvHomeViewModel2;

      @KeepFieldType
      TvOnboardingViewModel net_asksakis_massdroidv2_tv_ui_TvOnboardingViewModel2;

      @KeepFieldType
      TvArtistViewModel net_asksakis_massdroidv2_tv_ui_TvArtistViewModel2;

      @KeepFieldType
      TvQueueViewModel net_asksakis_massdroidv2_tv_ui_TvQueueViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // net.asksakis.massdroidv2.tv.ui.TvArtistViewModel 
          return (T) new TvArtistViewModel(singletonCImpl.provideMusicRepositoryProvider.get(), singletonCImpl.providePlayerRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 1: // net.asksakis.massdroidv2.tv.ui.TvHomeViewModel 
          return (T) new TvHomeViewModel(singletonCImpl.provideMusicRepositoryProvider.get(), singletonCImpl.providePlayerRepositoryProvider.get(), singletonCImpl.provideSettingsRepositoryProvider.get());

          case 2: // net.asksakis.massdroidv2.tv.ui.TvNowPlayingViewModel 
          return (T) new TvNowPlayingViewModel(singletonCImpl.providePlayerRepositoryProvider.get(), singletonCImpl.provideMusicRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 3: // net.asksakis.massdroidv2.tv.ui.TvOnboardingViewModel 
          return (T) new TvOnboardingViewModel(singletonCImpl.provideMaWebSocketClientProvider.get(), singletonCImpl.provideSettingsRepositoryProvider.get());

          case 4: // net.asksakis.massdroidv2.tv.ui.TvQueueViewModel 
          return (T) new TvQueueViewModel(singletonCImpl.providePlayerRepositoryProvider.get(), singletonCImpl.provideMusicRepositoryProvider.get());

          case 5: // net.asksakis.massdroidv2.tv.ui.TvRootViewModel 
          return (T) new TvRootViewModel(singletonCImpl.provideMaWebSocketClientProvider.get(), singletonCImpl.provideSettingsRepositoryProvider.get());

          case 6: // net.asksakis.massdroidv2.tv.ui.TvSettingsViewModel 
          return (T) new TvSettingsViewModel(singletonCImpl.provideSettingsRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends TvApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends TvApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectTvPlaybackService(TvPlaybackService arg0) {
      injectTvPlaybackService2(arg0);
    }

    private TvPlaybackService injectTvPlaybackService2(TvPlaybackService instance) {
      TvPlaybackService_MembersInjector.injectSendspinManager(instance, singletonCImpl.provideSendspinManagerProvider.get());
      TvPlaybackService_MembersInjector.injectSendspinVolumeCoordinator(instance, singletonCImpl.provideSendspinVolumeCoordinatorProvider.get());
      TvPlaybackService_MembersInjector.injectSettingsRepository(instance, singletonCImpl.provideSettingsRepositoryProvider.get());
      TvPlaybackService_MembersInjector.injectPlayerRepository(instance, singletonCImpl.providePlayerRepositoryProvider.get());
      TvPlaybackService_MembersInjector.injectWsClient(instance, singletonCImpl.provideMaWebSocketClientProvider.get());
      TvPlaybackService_MembersInjector.injectShortcutDispatcher(instance, singletonCImpl.shortcutActionDispatcherProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends TvApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Json> provideJsonProvider;

    private Provider<MaWebSocketClient> provideMaWebSocketClientProvider;

    private Provider<SettingsRepository> provideSettingsRepositoryProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<PlayHistoryRepository> providePlayHistoryRepositoryProvider;

    private Provider<SmartListeningRepository> provideSmartListeningRepositoryProvider;

    private Provider<LastFmGenreResolver> lastFmGenreResolverProvider;

    private Provider<SessionEventBus> sessionEventBusProvider;

    private Provider<MusicRepository> provideMusicRepositoryProvider;

    private Provider<QueueItemsCoordinator> queueItemsCoordinatorProvider;

    private Provider<PlayerRepository> providePlayerRepositoryProvider;

    private Provider<SendspinClient> provideSendspinClientProvider;

    private Provider<SendspinSyncEngine> provideSendspinSyncEngineProvider;

    private Provider<SendspinDirectEngine> provideSendspinDirectEngineProvider;

    private Provider<SendspinManager> provideSendspinManagerProvider;

    private Provider<SendspinVolumeCoordinator> provideSendspinVolumeCoordinatorProvider;

    private Provider<ShortcutActionDispatcher> shortcutActionDispatcherProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private PlayHistoryDao playHistoryDao() {
      return AppModule_ProvidePlayHistoryDaoFactory.providePlayHistoryDao(provideAppDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 1));
      this.provideJsonProvider = DoubleCheck.provider(new SwitchingProvider<Json>(singletonCImpl, 2));
      this.provideMaWebSocketClientProvider = DoubleCheck.provider(new SwitchingProvider<MaWebSocketClient>(singletonCImpl, 0));
      this.provideSettingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SettingsRepository>(singletonCImpl, 3));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 7));
      this.providePlayHistoryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PlayHistoryRepository>(singletonCImpl, 6));
      this.provideSmartListeningRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SmartListeningRepository>(singletonCImpl, 8));
      this.lastFmGenreResolverProvider = DoubleCheck.provider(new SwitchingProvider<LastFmGenreResolver>(singletonCImpl, 9));
      this.sessionEventBusProvider = DoubleCheck.provider(new SwitchingProvider<SessionEventBus>(singletonCImpl, 10));
      this.provideMusicRepositoryProvider = new DelegateFactory<>();
      this.queueItemsCoordinatorProvider = DoubleCheck.provider(new SwitchingProvider<QueueItemsCoordinator>(singletonCImpl, 11));
      this.providePlayerRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PlayerRepository>(singletonCImpl, 5));
      DelegateFactory.setDelegate(provideMusicRepositoryProvider, DoubleCheck.provider(new SwitchingProvider<MusicRepository>(singletonCImpl, 4)));
      this.provideSendspinClientProvider = DoubleCheck.provider(new SwitchingProvider<SendspinClient>(singletonCImpl, 13));
      this.provideSendspinSyncEngineProvider = DoubleCheck.provider(new SwitchingProvider<SendspinSyncEngine>(singletonCImpl, 14));
      this.provideSendspinDirectEngineProvider = DoubleCheck.provider(new SwitchingProvider<SendspinDirectEngine>(singletonCImpl, 15));
      this.provideSendspinManagerProvider = DoubleCheck.provider(new SwitchingProvider<SendspinManager>(singletonCImpl, 12));
      this.provideSendspinVolumeCoordinatorProvider = DoubleCheck.provider(new SwitchingProvider<SendspinVolumeCoordinator>(singletonCImpl, 16));
      this.shortcutActionDispatcherProvider = DoubleCheck.provider(new SwitchingProvider<ShortcutActionDispatcher>(singletonCImpl, 17));
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @Override
    public void injectTvApp(TvApp arg0) {
      injectTvApp2(arg0);
    }

    private TvApp injectTvApp2(TvApp instance) {
      TvApp_MembersInjector.injectWsClient(instance, provideMaWebSocketClientProvider.get());
      TvApp_MembersInjector.injectSettingsRepository(instance, provideSettingsRepositoryProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // net.asksakis.massdroidv2.data.websocket.MaWebSocketClient 
          return (T) AppModule_ProvideMaWebSocketClientFactory.provideMaWebSocketClient(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideJsonProvider.get());

          case 1: // okhttp3.OkHttpClient 
          return (T) AppModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 2: // kotlinx.serialization.json.Json 
          return (T) AppModule_ProvideJsonFactory.provideJson();

          case 3: // net.asksakis.massdroidv2.domain.repository.SettingsRepository 
          return (T) RepositoryModule_ProvideSettingsRepositoryFactory.provideSettingsRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // net.asksakis.massdroidv2.domain.repository.MusicRepository 
          return (T) RepositoryModule_ProvideMusicRepositoryFactory.provideMusicRepository(singletonCImpl.provideMaWebSocketClientProvider.get(), singletonCImpl.provideJsonProvider.get(), DoubleCheck.lazy(singletonCImpl.providePlayerRepositoryProvider));

          case 5: // net.asksakis.massdroidv2.domain.repository.PlayerRepository 
          return (T) RepositoryModule_ProvidePlayerRepositoryFactory.providePlayerRepository(singletonCImpl.provideMaWebSocketClientProvider.get(), singletonCImpl.provideJsonProvider.get(), singletonCImpl.providePlayHistoryRepositoryProvider.get(), singletonCImpl.provideSettingsRepositoryProvider.get(), singletonCImpl.provideSmartListeningRepositoryProvider.get(), singletonCImpl.lastFmGenreResolverProvider.get(), singletonCImpl.sessionEventBusProvider.get(), singletonCImpl.queueItemsCoordinatorProvider.get());

          case 6: // net.asksakis.massdroidv2.domain.repository.PlayHistoryRepository 
          return (T) RepositoryModule_ProvidePlayHistoryRepositoryFactory.providePlayHistoryRepository(singletonCImpl.playHistoryDao(), singletonCImpl.provideJsonProvider.get(), singletonCImpl.provideAppDatabaseProvider.get());

          case 7: // net.asksakis.massdroidv2.data.database.AppDatabase 
          return (T) AppModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // net.asksakis.massdroidv2.domain.repository.SmartListeningRepository 
          return (T) RepositoryModule_ProvideSmartListeningRepositoryFactory.provideSmartListeningRepository(singletonCImpl.playHistoryDao(), singletonCImpl.provideSettingsRepositoryProvider.get(), singletonCImpl.provideAppDatabaseProvider.get());

          case 9: // net.asksakis.massdroidv2.data.lastfm.LastFmGenreResolver 
          return (T) new LastFmGenreResolver(singletonCImpl.playHistoryDao(), singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideJsonProvider.get(), singletonCImpl.provideSettingsRepositoryProvider.get());

          case 10: // net.asksakis.massdroidv2.data.websocket.SessionEventBus 
          return (T) new SessionEventBus();

          case 11: // net.asksakis.massdroidv2.data.repository.queue.QueueItemsCoordinator 
          return (T) new QueueItemsCoordinator(DoubleCheck.lazy(singletonCImpl.provideMusicRepositoryProvider));

          case 12: // net.asksakis.massdroidv2.data.sendspin.SendspinManager 
          return (T) AppModule_ProvideSendspinManagerFactory.provideSendspinManager(singletonCImpl.provideSendspinClientProvider.get(), singletonCImpl.provideSendspinSyncEngineProvider.get(), singletonCImpl.provideSendspinDirectEngineProvider.get(), singletonCImpl.sessionEventBusProvider.get());

          case 13: // net.asksakis.massdroidv2.data.sendspin.SendspinClient 
          return (T) AppModule_ProvideSendspinClientFactory.provideSendspinClient(singletonCImpl.provideMaWebSocketClientProvider.get(), singletonCImpl.provideJsonProvider.get());

          case 14: // net.asksakis.massdroidv2.data.sendspin.SendspinSyncEngine 
          return (T) AppModule_ProvideSendspinSyncEngineFactory.provideSendspinSyncEngine(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 15: // net.asksakis.massdroidv2.data.sendspin.SendspinDirectEngine 
          return (T) AppModule_ProvideSendspinDirectEngineFactory.provideSendspinDirectEngine(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 16: // net.asksakis.massdroidv2.data.sendspin.SendspinVolumeCoordinator 
          return (T) AppModule_ProvideSendspinVolumeCoordinatorFactory.provideSendspinVolumeCoordinator(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSendspinManagerProvider.get(), singletonCImpl.provideSettingsRepositoryProvider.get(), singletonCImpl.providePlayerRepositoryProvider.get());

          case 17: // net.asksakis.massdroidv2.domain.shortcut.ShortcutActionDispatcher 
          return (T) new ShortcutActionDispatcher();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
