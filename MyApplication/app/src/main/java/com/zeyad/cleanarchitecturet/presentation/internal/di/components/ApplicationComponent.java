package com.zeyad.cleanarchitecturet.presentation.internal.di.components;

import android.content.Context;

import com.firebase.client.Firebase;
import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecturet.domain.repositories.Repository;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;
import com.zeyad.cleanarchitecturet.presentation.internal.di.modules.ApplicationModule;
import com.zeyad.cleanarchitecturet.presentation.view.activities.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the application.
 */
@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(BaseActivity baseActivity);

    //Exposed to sub-graphs.
    Context context();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    UserRepository userRepository();

//    Repository repository();

    Firebase firebase();

//    void inject(ImageDownloadIntentService imageDownloadIntentService);
}
