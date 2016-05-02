package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.models.mapper.ModelDataMapper;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * This class is a general implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
public class GenericUseCase extends BaseUseCase {

    private final Repository repository;
    private final ModelDataMapper modelDataMapper;

    @Inject
    public GenericUseCase(Repository repository, ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
        this.modelDataMapper = new ModelDataMapper();
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }

    @Override
    public Observable buildUseCaseObservableList(Class presentationClass, Class domainClass, Class dataClass) {
        return repository.collection(presentationClass, domainClass, dataClass)
                .map(collection -> modelDataMapper.transformAllToPresentation(collection, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.getById(itemId, presentationClass, domainClass, dataClass)
                .map(item -> modelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservablePut(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.put(object, presentationClass, domainClass, dataClass)
                .map(item -> modelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservableDeleteMultiple(List list, Class domainClass, Class dataClass) {
        return repository.deleteCollection(list, domainClass, dataClass);
    }

    @Override
    public Observable buildUseCaseObservableQuery(String query, String column, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.search(query, column, presentationClass, domainClass, dataClass)
                .map(collection -> modelDataMapper.transformAllToPresentation((Collection) collection, presentationClass));
    }
}