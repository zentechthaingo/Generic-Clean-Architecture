package com.zeyad.cleanarchetecturet.presentation.internal.di.modules;

import android.content.Context;

import com.zeyad.cleanarchetecturet.data.cache.UserCache;
import com.zeyad.cleanarchetecturet.data.cache.UserCacheImpl;
import com.zeyad.cleanarchetecturet.data.executor.JobExecutor;
import com.zeyad.cleanarchetecturet.data.net.RestApi;
import com.zeyad.cleanarchetecturet.data.repository.UserDataRepository;
import com.zeyad.cleanarchetecturet.domain.executor.PostExecutionThread;
import com.zeyad.cleanarchetecturet.domain.executor.ThreadExecutor;
import com.zeyad.cleanarchetecturet.domain.repository.UserRepository;
import com.zeyad.cleanarchetecturet.presentation.AndroidApplication;
import com.zeyad.cleanarchetecturet.presentation.UIThread;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

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
    UserCache provideUserCache(UserCacheImpl userCache) {
        return userCache;
    }

    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDataRepository userDataRepository) {
        return userDataRepository;
    }
}