package com.zeyad.cleanarchitecture.data.repository;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class DataRepository implements Repository {

    private final DataStoreFactory dataStoreFactory;
    private EntityMapper mEntityDataMapper;

    /**
     * Constructs a {@link Repository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     */
    @Inject
    public DataRepository(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
        this.mEntityDataMapper = new EntityDataMapper();
    }

    @Override
    @RxLogObservable
    public Observable<List> collection(Class presentationClass, Class domainClass, Class dataClass) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        return dataStoreFactory.getAll(mEntityDataMapper).collection(domainClass, dataClass);
    }

    @Override
    @RxLogObservable
    public Observable<?> getById(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        return dataStoreFactory.getById(itemId, mEntityDataMapper, dataClass)
                .getById(itemId, domainClass, dataClass);
    }

    @Override
    @RxLogObservable
    public Observable<List> dynamicCollection(String url, Class presentationClass, Class domainClass, Class dataClass) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        return dataStoreFactory.getAllDynamic(mEntityDataMapper).dynamicList(url, domainClass, dataClass);
    }

    @Override
    @RxLogObservable
    public Observable<?> dynamicObject(String url, Class presentationClass, Class domainClass, Class dataClass) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        return dataStoreFactory.getObjectDynamic(-1, mEntityDataMapper, dataClass).dynamicObject(url,
                domainClass, dataClass);
    }

    @Override
    @RxLogObservable
    public Observable<?> put(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        return Observable
                .concat(dataStoreFactory
                                .putToDisk(mEntityDataMapper)
                                .putToDisk(object, dataClass),
                        dataStoreFactory
                                .putToCloud(mEntityDataMapper)
                                .postToCloud(object, domainClass, dataClass))
                .distinct();
    }

    @Override
    @RxLogObservable
    public Observable<?> deleteCollection(List<Integer> list, Class domainClass, Class dataClass) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        return Observable
                .merge(dataStoreFactory
                                .deleteCollectionFromCloud(mEntityDataMapper)
                                .deleteCollectionFromCloud(list, domainClass, dataClass),
                        dataStoreFactory
                                .deleteCollectionFromDisk(mEntityDataMapper)
                                .deleteCollectionFromDisk(list, dataClass))
                .distinct();
    }

    @Override
    @RxLogObservable
    public Observable<List> search(String query, String column, Class presentationClass, Class domainClass,
                                   Class dataClass) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        return dataStoreFactory
                .searchCloud(mEntityDataMapper)
                .searchCloud(query, domainClass, dataClass)
                .mergeWith(dataStoreFactory
                        .searchDisk(mEntityDataMapper)
                        .searchDisk(query, column, domainClass, dataClass))
                .distinct();
    }
}