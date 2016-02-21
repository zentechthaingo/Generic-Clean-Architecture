package com.zeyad.cleanarchetecturet.domain.interactor;

import com.zeyad.cleanarchetecturet.domain.User;
import com.zeyad.cleanarchetecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchetecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchetecturet.domain.repositories.UserRepository;

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
        return this.userRepository.users();
    }
}