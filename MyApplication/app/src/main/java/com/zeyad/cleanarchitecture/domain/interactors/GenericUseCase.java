package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.models.mapper.ModelDataMapper;
import com.zeyad.cleanarchitecture.domain.repository.Repository;

import java.util.HashMap;

import javax.inject.Inject;

import io.realm.RealmQuery;
import rx.Observable;

/**
 * This class is a general implementation of {@link BaseUseCase} that represents a use case for
 * retrieving data related to an specific {@link User}.
 */
public class GenericUseCase extends BaseUseCase {

    private final Repository mRepository;
    private final ModelDataMapper mModelDataMapper;

    @Inject
    public GenericUseCase(Repository repository, ThreadExecutor threadExecutor,
                          PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        mRepository = repository;
        mModelDataMapper = new ModelDataMapper();
    }

    @Override
    protected Observable buildUseCaseObservableDynamicList(String url, Class presentationClass,
                                                           Class domainClass, Class dataClass,
                                                           boolean persist) {
        return mRepository.dynamicList(url, domainClass, dataClass, persist)
                .map(list -> mModelDataMapper.transformAllToPresentation(list, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservableDynamicObjectById(String url, String idColumnName,
                                                                 int itemId, Class presentationClass,
                                                                 Class domainClass, Class dataClass,
                                                                 boolean persist) {
        return mRepository.getObjectDynamicallyById(url, idColumnName, itemId, domainClass, dataClass, persist)
                .map(item -> mModelDataMapper.transformToPresentation(item, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservablePut(String url, HashMap<String, Object> keyValuePairs,
                                                   Class presentationClass, Class domainClass,
                                                   Class dataClass, boolean persist) {
        return mRepository.postObjectDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass));
    }

    @Override
    protected Observable buildUseCaseObservableDynamicPostList(String url, HashMap<String, Object> keyValuePairs,
                                                               Class presentationClass, Class domainClass,
                                                               Class dataClass, boolean persist) {
        return mRepository.postListDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservableDeleteMultiple(String url, HashMap<String, Object> keyValuePairs,
                                                           Class domainClass, Class dataClass, boolean persist) {
        return mRepository.deleteListDynamically(url, keyValuePairs, domainClass, dataClass, persist);
    }

    @Override
    public Observable buildUseCaseObservableQuery(String query, String column, Class presentationClass,
                                                  Class domainClass, Class dataClass) {
        return mRepository.searchDisk(query, column, domainClass, dataClass)
                .map(list -> mModelDataMapper.transformAllToPresentation(list, presentationClass));
    }

    @Override
    public Observable buildUseCaseObservableRealmQuery(RealmQuery realmQuery, Class presentationClass,
                                                       Class domainClass) {
        return mRepository.searchDisk(realmQuery, domainClass)
                .map(list -> mModelDataMapper.transformAllToPresentation(list, presentationClass));
    }
}