package com.zeyad.cleanarchitecture.presentation.di.modules;

import android.support.v7.app.AppCompatActivity;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.repository.Repository;
import com.zeyad.cleanarchitecture.presentation.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.navigation.Navigator;

import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Activity state and expose it to the graph.
 */
@Module
public class ActivityModule {
    private final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    /**
     * Expose the activity to dependents in the graph.
     */
    @Provides
    @PerActivity
    AppCompatActivity activity() {
        return this.activity;
    }

    @Provides
    @PerActivity
    GenericUseCase providesGenericUseCase(Repository repository, ThreadExecutor threadExecutor,
                                          PostExecutionThread postExecutionThread) {
        return new GenericUseCase(repository, threadExecutor, postExecutionThread);
    }

    @Provides
    @PerActivity
    Navigator providesNavigator() {
        return new Navigator();
    }
}