package com.zeyad.cleanarchitecture.presentation.internal.di.modules;

import android.support.v7.app.AppCompatActivity;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.interactor.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.navigation.Navigator;

import javax.inject.Named;

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
    @Named("generalizedUseCase")
    GenericUseCase providesGetGeneralListUseCase(Repository repository, ThreadExecutor threadExecutor,
                                                     PostExecutionThread postExecutionThread) {
        return new GenericUseCase(repository, threadExecutor, postExecutionThread);
    }

    @Provides
    @PerActivity
    Navigator providesNavigator() {
        return new Navigator();
    }
}