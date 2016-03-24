package com.zeyad.cleanarchitecturet.domain.interactor;

import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecturet.domain.models.User;
import com.zeyad.cleanarchitecturet.domain.repositories.Repository;

import javax.inject.Inject;

import rx.Observable;

/**
 * This class is a general implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
public class GeneralizedUseCase extends BaseUseCase {

    private final Repository repository;

    @Inject
    public GeneralizedUseCase(Repository repository, ThreadExecutor threadExecutor,
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
        return repository.item(itemId, presentationClass, domainClass, dataClass);
    }
}