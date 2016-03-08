package com.zeyad.cleanarchitecturet.domain.interactor;

import com.zeyad.cleanarchitecturet.domain.User;
import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;

import javax.inject.Inject;

import rx.Observable;

/**
 * This class is an implementation of {@link UseCase} that represents a use case for
 * retrieving a collection of all {@link User}.
 */
public class GetUserList extends UseCase {

    private final UserRepository userRepository;

    @Inject
    public GetUserList(UserRepository userRepository, ThreadExecutor threadExecutor,
                       PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return userRepository.users();
    }
}