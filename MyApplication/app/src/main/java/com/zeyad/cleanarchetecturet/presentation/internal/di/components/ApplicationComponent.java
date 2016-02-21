package com.zeyad.cleanarchetecturet.presentation.internal.di.components;

import android.content.Context;

import com.firebase.client.Firebase;
import com.zeyad.cleanarchetecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchetecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchetecturet.domain.repositories.UserRepository;
import com.zeyad.cleanarchetecturet.presentation.internal.di.modules.ApplicationModule;
import com.zeyad.cleanarchetecturet.presentation.view.activities.BaseActivity;

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

    Firebase firebase();
}
