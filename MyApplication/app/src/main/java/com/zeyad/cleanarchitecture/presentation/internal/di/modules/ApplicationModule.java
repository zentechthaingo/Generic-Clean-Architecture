package com.zeyad.cleanarchitecture.presentation.internal.di.modules;

import android.content.Context;

import com.firebase.client.Firebase;
import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.db.RealmManagerImpl;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManagerImpl;
import com.zeyad.cleanarchitecture.data.executor.JobExecutor;
import com.zeyad.cleanarchitecture.data.repository.DataRepository;
import com.zeyad.cleanarchitecture.data.repository.UserDataRepository;
import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;
import com.zeyad.cleanarchitecture.presentation.AndroidApplication;
import com.zeyad.cleanarchitecture.presentation.UIThread;
import com.zeyad.cleanarchitecture.utilities.Constants;

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
    GeneralRealmManager provideGeneralRealmManager(GeneralRealmManagerImpl generalRealmManager) {
        return generalRealmManager;
    }


    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDataRepository userDataRepository) {
        return userDataRepository;
    }

    @Provides
    @Singleton
    Repository provideRepository(DataRepository dataRepository) {
        return dataRepository;
    }

    @Provides
    @Singleton
    Firebase provideFirebase() {
        return new Firebase(Constants.FIREBASE_URL);
    }
}