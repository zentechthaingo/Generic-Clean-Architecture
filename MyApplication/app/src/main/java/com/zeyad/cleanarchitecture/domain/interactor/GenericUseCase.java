package com.zeyad.cleanarchitecture.domain.interactor;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;

/**
 * This class is a general implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
// FIXME: 3/28/16 Test!
public class GenericUseCase extends BaseUseCase {

    private final Repository repository;

    @Inject
    public GenericUseCase(Repository repository, ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }

    @Override
    public Observable buildUseCaseObservableList(Class presentationClass, Class domainClass, Class dataClass) {
        return repository.Collection(presentationClass, domainClass, dataClass);
    }

    @Override
    public Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.getById(itemId, presentationClass, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservablePut(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.put(object, domainClass, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableDelete(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.delete(object, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableDelete(long itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.delete(itemId, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableDeleteMultiple(Collection collection, Class presentationClass,
                                                              Class domainClass, Class dataClass) {
        return repository.deleteCollection(collection, dataClass);
    }

    @Override
    protected Observable buildUseCaseObservableQuery(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.search();
    }
}