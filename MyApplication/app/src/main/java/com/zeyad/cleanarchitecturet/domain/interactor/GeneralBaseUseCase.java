package com.zeyad.cleanarchitecturet.domain.interactor;

import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecturet.domain.models.User;
import com.zeyad.cleanarchitecturet.domain.repositories.Repository;

import rx.Observable;

/**
 * This class is a general implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
public class GeneralBaseUseCase extends BaseUseCase {

    private final Repository repository;

    //    @Inject
    public GeneralBaseUseCase(Repository repository, ThreadExecutor threadExecutor,
                              PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }

    public Observable buildUseCaseObservableList(Class clazz) {
        return repository.list(clazz);
    }

    public Observable buildUseCaseObservableDetail(int itemId, Class clazz) {
        return repository.item(itemId, clazz);
    }
}