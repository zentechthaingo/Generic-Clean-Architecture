package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.models.mapper.ModelDataMapper;
import com.zeyad.cleanarchitecture.domain.repository.Repository;

import java.util.HashMap;
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
    public Observable buildUseCaseObservableList(Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
        return repository.collection(presentationClass, domainClass, dataClass, persist)
                .map(collection -> modelDataMapper.transformAllToPresentation(collection, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservableDetail(int itemId, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
        return repository.getById(itemId, presentationClass, domainClass, dataClass, persist)
                .map(item -> modelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservableDynamicList(String url, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
        return repository.dynamicCollection(url, presentationClass, domainClass, dataClass, persist)
                .map(collection -> modelDataMapper.transformAllToPresentation(collection, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservableDynamicObject(String url, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
        return repository.dynamicObject(url, presentationClass, domainClass, dataClass, persist)
                .map(item -> modelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservableDynamicPost(String url, HashMap<String, Object> keyValuePairs,
                                                           Class presentationClass, Class domainClass) {
        return repository.dynamicPost(url, keyValuePairs, presentationClass, domainClass)
                .map(object -> modelDataMapper.transformToPresentation(object, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservablePut(Object object, Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
        return repository.put(object, presentationClass, domainClass, dataClass, persist)
                .map(item -> modelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservableDeleteMultiple(List<Integer> list, Class domainClass, Class dataClass, boolean persist) {
        return repository.deleteCollection(list, domainClass, dataClass, persist);
    }

    @Override
    public Observable buildUseCaseObservableQuery(String query, String column, Class presentationClass, Class domainClass, Class dataClass) {
        return repository.search(query, column, presentationClass, domainClass, dataClass)
                .map(list -> modelDataMapper.transformAllToPresentation(list, presentationClass));
    }
}