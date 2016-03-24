package com.zeyad.cleanarchitecturet.presentation.internal.di.modules;

import android.support.v7.app.AppCompatActivity;

import com.zeyad.cleanarchitecturet.presentation.internal.di.PerActivity;

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
}
