package com.zeyad.cleanarchitecture.data.repository;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class DataRepository implements Repository {

    private final DataStoreFactory dataStoreFactory;
    private final EntityDataMapper entityDataMapper;

    /**
     * Constructs a {@link UserRepository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     * @param entityDataMapper {@link UserEntityDataMapper}.
     */
    @Inject
    public DataRepository(DataStoreFactory dataStoreFactory, EntityDataMapper entityDataMapper) {
        this.dataStoreFactory = dataStoreFactory;
        this.entityDataMapper = entityDataMapper;
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
    @RxLogObservable
    public Observable<List> collection(Class presentationClass, Class domainClass, Class dataClass) {
        return dataStoreFactory.getAll(entityDataMapper).collection(domainClass, dataClass);
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
    @RxLogObservable
    public Observable<?> getById(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return dataStoreFactory.getById(itemId, entityDataMapper, dataClass)
                .getById(itemId, domainClass, dataClass);
    }

    @Override
    @RxLogObservable
    public Observable<?> put(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable
                .concat(dataStoreFactory
                                .putToDisk(entityDataMapper)
                                .putToDisk(object, dataClass),
                        dataStoreFactory
                                .putToCloud(entityDataMapper)
                                .postToCloud(object, domainClass, dataClass))
                .distinct();
    }

    @Override
    @RxLogObservable
    public Observable<?> deleteCollection(List<Integer> list, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .deleteCollectionFromCloud(entityDataMapper)
                                .deleteCollectionFromCloud(list, domainClass, dataClass),
                        dataStoreFactory
                                .deleteCollectionFromDisk(entityDataMapper)
                                .deleteCollectionFromDisk(list, dataClass))
                .distinct();
    }

    @Override
    @RxLogObservable
    public Observable<?> search(String query, String column, Class presentationClass, Class domainClass,
                                Class dataClass) {
        return dataStoreFactory
                .searchCloud(entityDataMapper)
                .searchCloud(query, domainClass, dataClass)
                .mergeWith(dataStoreFactory
                        .searchDisk(entityDataMapper)
                        .searchDisk(query, column, domainClass, dataClass))
                .distinct();
    }
}