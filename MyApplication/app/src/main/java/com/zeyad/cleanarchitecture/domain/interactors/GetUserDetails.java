package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * This class is an implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
public class GetUserDetails extends BaseUseCase {

    private final int userId;
    private final UserRepository userRepository;

    @Inject
    public GetUserDetails(int userId, UserRepository userRepository, ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userId = userId;
        this.userRepository = userRepository;
    }

    @Override
    public Observable buildUseCaseObservable() {
        return userRepository.user(userId);
    }

    @Override
    protected Observable buildUseCaseObservableList(Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant getById list from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant getById detail from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservablePut(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant put object from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableDeleteMultiple(List list, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant delete collection from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableQuery(String query, String column, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant search object from GetUserDetails"));
    }
}