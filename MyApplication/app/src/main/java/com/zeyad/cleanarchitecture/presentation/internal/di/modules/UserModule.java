package com.zeyad.cleanarchitecture.presentation.internal.di.modules;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.interactor.BaseUseCase;
import com.zeyad.cleanarchitecture.domain.interactor.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.interactor.GetUserDetails;
import com.zeyad.cleanarchitecture.domain.interactor.GetUserList;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;

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

    // TODO: 3/25/16 Deprecate!
    @Provides
    @PerActivity
    @Named("userEntityList")
    BaseUseCase provideGetUserListUseCase(GetUserList getUserList) {
        return getUserList;
    }

    // TODO: 3/25/16 Deprecate!
    @Provides
    @PerActivity
    @Named("userDetails")
    BaseUseCase provideGetUserDetailsUseCase(UserRepository userRepository, ThreadExecutor threadExecutor,
                                             PostExecutionThread postExecutionThread) {
        return new GetUserDetails(userId, userRepository, threadExecutor, postExecutionThread);
    }

    @Provides
    @PerActivity
    @Named("generalizedDetailUseCase")
    GenericUseCase provideGetGeneralListUseCase(Repository repository, ThreadExecutor threadExecutor,
                                                    PostExecutionThread postExecutionThread) {
        return new GenericUseCase(repository, threadExecutor, postExecutionThread);
    }
}