package com.zeyad.cleanarchitecturet.domain.interactor;

import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecturet.domain.models.User;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * This class is an implementation of {@link BaseUseCase} that represents a use case for
 * retrieving a collection of all {@link User}.
 */
public class GetUserList extends BaseUseCase {

    private final UserRepository userRepository;

    @Inject
    public GetUserList(UserRepository userRepository, ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    // TODO: 3/22/16 Try it out!
    @Override
    public Observable buildUseCaseObservable() {
        return userRepository.users();
//                .map(users -> userModelDataMapper.transformToDomain(users));
    }

    @Override
    protected Observable buildUseCaseObservableList(Class presentationClass, Class domainClass, Class dataClass) {
        return null;
    }

    @Override
    protected Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return null;
    }
}