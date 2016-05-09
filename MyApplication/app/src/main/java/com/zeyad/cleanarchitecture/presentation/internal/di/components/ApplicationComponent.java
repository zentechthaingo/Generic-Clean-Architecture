package com.zeyad.cleanarchitecture.presentation.internal.di.components;

import android.content.Context;

import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.ApplicationModule;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;
import com.zeyad.cleanarchitecture.presentation.views.activities.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);

    void inject(GenericNetworkQueueIntentService genericNetworkQueueIntentService);

    //Exposed to sub-graphs.
    Context context();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    UserRepository userRepository();

    Repository repository();

    Firebase firebase();

    RxEventBus rxEventBus();

    Gson gson();
}