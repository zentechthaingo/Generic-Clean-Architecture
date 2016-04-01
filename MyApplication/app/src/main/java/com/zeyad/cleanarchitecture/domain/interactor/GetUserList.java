package com.zeyad.cleanarchitecture.domain.interactor;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;

import java.util.Collection;

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
        return Observable.error(new Exception("cant get list from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant get detail from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservablePut(Object object, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant put object from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableDelete(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant delete object from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableDelete(long itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant delete object from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableDeleteMultiple(Collection collection, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant delete collection from GetUserDetails"));
    }

    @Override
    protected Observable buildUseCaseObservableQuery(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant search object from GetUserDetails"));
    }
}