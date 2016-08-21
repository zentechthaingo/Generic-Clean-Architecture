package com.zeyad.cleanarchitecture.presentation.di.modules;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.repository.Repository;
import com.zeyad.cleanarchitecture.presentation.di.PerActivity;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module that provides user related collaborators.
 */
@Module
public class UserModule {

    private int userId = -1;

    public UserModule() {
    }

    public UserModule(int userId) {
        this.userId = userId;
    }

    @Provides
    @PerActivity
    @Named("generalizedDetailUseCase")
    GenericUseCase provideGetGeneralListUseCase(Repository repository, ThreadExecutor threadExecutor,
                                                PostExecutionThread postExecutionThread) {
        return new GenericUseCase(repository, threadExecutor, postExecutionThread);
    }
}