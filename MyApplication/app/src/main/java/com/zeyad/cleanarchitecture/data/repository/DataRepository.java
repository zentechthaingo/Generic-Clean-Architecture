package com.zeyad.cleanarchitecture.data.repository;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.repository.Repository;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class DataRepository implements Repository {

    private final DataStoreFactory mDataStoreFactory;
    private EntityMapper mEntityDataMapper;

    /**
     * Constructs a {@link Repository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     */
    @Inject
    public DataRepository(DataStoreFactory dataStoreFactory) {
        mDataStoreFactory = dataStoreFactory;
        mEntityDataMapper = new UserEntityDataMapper();
    }

    @Override
    @RxLogObservable
    public Observable<List> collection(Class presentationClass, Class domainClass, Class dataClass,
                                       boolean persist) {
        return mDataStoreFactory.getAll(Utils.getDataMapper(dataClass)).collection(domainClass, dataClass, persist);
    }

    @Override
    @RxLogObservable
    public Observable<?> getById(int itemId, Class presentationClass, Class domainClass, Class dataClass,
                                 boolean persist) {
        return mDataStoreFactory.getById(itemId, Utils.getDataMapper(dataClass), dataClass)
                .getById(itemId, domainClass, dataClass, persist);
    }

    @Override
    @RxLogObservable
    public Observable<List> dynamicCollection(String url, Class presentationClass, Class domainClass,
                                              Class dataClass, boolean persist) {
        return mDataStoreFactory.getAllDynamicallyFromCloud(Utils.getDataMapper(dataClass)).dynamicList(url, domainClass, dataClass, persist);
    }

    @Override
    @RxLogObservable
    public Observable<?> dynamicObject(String url, Class presentationClass, Class domainClass, Class dataClass,
                                       boolean persist) {
        return mDataStoreFactory.getObjectDynamicallyFromCloud(Utils.getDataMapper(dataClass))
                .dynamicObject(url, domainClass, dataClass, persist);
    }

    @Override
    @RxLogObservable
    public Observable<?> dynamicPost(String url, HashMap<String, Object> keyValuePairs,
                                     Class presentationClass, Class domainClass) {
        return mDataStoreFactory.dynamicPost(mEntityDataMapper).dynamicPost(url, keyValuePairs,
                domainClass);
    }

    @Override
    @RxLogObservable
    public Observable<?> put(Object object, Class presentationClass, Class domainClass, Class dataClass,
                             boolean persist) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        if (persist)
            return Observable
                    .concat(mDataStoreFactory
                                    .putToDisk(mEntityDataMapper)
                                    .putToDisk(object, dataClass),
                            mDataStoreFactory
                                    .putToCloud(mEntityDataMapper)
                                    .putToCloud(object, domainClass, dataClass, true))
                    .distinct();
        return mDataStoreFactory.putToCloud(mEntityDataMapper).putToCloud(object, domainClass,
                dataClass, false);

    }

    @Override
    @RxLogObservable
    public Observable<?> deleteCollection(List<Integer> list, Class domainClass, Class dataClass,
                                          boolean persist) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        if (persist)
            return Observable
                    .merge(mDataStoreFactory
                                    .deleteCollectionFromCloud(mEntityDataMapper)
                                    .deleteCollectionFromCloud(list, domainClass, dataClass, true),
                            mDataStoreFactory
                                    .deleteCollectionFromDisk(mEntityDataMapper)
                                    .deleteCollectionFromDisk(list, dataClass))
                    .distinct();
        return mDataStoreFactory.deleteCollectionFromCloud(mEntityDataMapper)
                .deleteCollectionFromCloud(list, domainClass, dataClass, false);
    }

    @Override
    @RxLogObservable
    public Observable<List> search(String query, String column, Class presentationClass, Class domainClass,
                                   Class dataClass) {
        return mDataStoreFactory.search(Utils.getDataMapper(dataClass)).search(query, column, domainClass, dataClass);
    }
}