package com.zeyad.cleanarchetecturet.presentation.internal.di.modules;

import android.content.Context;

import com.zeyad.cleanarchetecturet.data.db.RealmManager;
import com.zeyad.cleanarchetecturet.data.db.RealmManagerImpl;
import com.zeyad.cleanarchetecturet.data.executor.JobExecutor;
import com.zeyad.cleanarchetecturet.data.repository.UserDataRepository;
import com.zeyad.cleanarchetecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchetecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchetecturet.domain.repositories.UserRepository;
import com.zeyad.cleanarchetecturet.presentation.AndroidApplication;
import com.zeyad.cleanarchetecturet.presentation.UIThread;

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
}