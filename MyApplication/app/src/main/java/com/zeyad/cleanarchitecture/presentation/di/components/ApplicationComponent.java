package com.zeyad.cleanarchitecture.presentation.di.components;

import android.content.Context;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.repository.Repository;
import com.zeyad.cleanarchitecture.presentation.broadcastreceivers.NetworkChangeReceiver;
import com.zeyad.cleanarchitecture.presentation.di.modules.ApplicationModule;
import com.zeyad.cleanarchitecture.presentation.screens.BaseActivity;
import com.zeyad.cleanarchitecture.presentation.screens.BaseFragment;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

    void inject(NetworkChangeReceiver networkChangeReceiver);

    void inject(GenericNetworkQueueIntentService genericNetworkQueueIntentService);

    //Exposed to sub-graphs.
    Context context();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    Repository repository();

    RxEventBus rxEventBus();

    Gson gson();
}