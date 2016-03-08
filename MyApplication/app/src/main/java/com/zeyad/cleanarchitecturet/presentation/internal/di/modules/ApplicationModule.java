package com.zeyad.cleanarchitecturet.presentation.internal.di.modules;

import android.content.Context;

import com.firebase.client.Firebase;
import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.db.RealmManagerImpl;
import com.zeyad.cleanarchitecturet.data.executor.JobExecutor;
import com.zeyad.cleanarchitecturet.data.repository.UserDataRepository;
import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;
import com.zeyad.cleanarchitecturet.presentation.AndroidApplication;
import com.zeyad.cleanarchitecturet.presentation.UIThread;
import com.zeyad.cleanarchitecturet.utilities.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides objects which will live during the application lifecycle.
 */
@Module
public class ApplicationModule {
    private final AndroidApplication application;

    public ApplicationModule(AndroidApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return this.application;
    }

    @Provides
    @Singleton
    ThreadExecutor provideThreadExecutor(JobExecutor jobExecutor) {
        return jobExecutor;
    }

    @Provides
    @Singleton
    PostExecutionThread providePostExecutionThread(UIThread uiThread) {
        return uiThread;
    }

    @Provides
    @Singleton
    RealmManager provideRealmManager(RealmManagerImpl realmManager) {
        return realmManager;
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDataRepository userDataRepository) {
        return userDataRepository;
    }

    @Provides
    @Singleton
    Firebase provideFirebase() {
        return new Firebase(Constants.FIREBASE_URL);
    }
}