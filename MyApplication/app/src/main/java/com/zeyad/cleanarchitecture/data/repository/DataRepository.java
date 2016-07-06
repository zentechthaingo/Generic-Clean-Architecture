package com.zeyad.cleanarchitecture.data.repository;

import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.repository.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.repository.Repository;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmQuery;
import rx.Observable;

// TODO: 13/05/16 Document!
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
        mEntityDataMapper = new EntityDataMapper();
    }

    /**
     * Returns a list of object of desired type by providing the classes.
     * If the url is empty, it will fetch the data from the local db.
     * If persist is true its going to save the list to the database, given it fetched from the cloud.
     *
     * @param url         end point.
     * @param domainClass The domain class representation of the object.
     * @param dataClass   The data class representation of the object.
     * @param persist     boolean to decide whether to persist the result or not
     * @return A list, if available.
     */
    @Override
//    @RxLogObservable
    public Observable<List> getListDynamically(String url, Class domainClass, Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicList(url, domainClass, dataClass, persist);
    }

    /**
     * Returns a list of object of desired type by providing the classes.
     * If the url is empty, it will fetch the data from the local db.
     * If persist is true its going to save the list to the database, given it fetched from the cloud.
     *
     * @param url         end point.
     * @param domainClass The domain class representation of the object.
     * @param dataClass   The data class representation of the object.
     * @param persist     boolean to decide whether to persist the result or not
     * @param shouldCache boolean to decide whether to cache network response or not
     * @return A list, if available.
     */
    @Override
//    @RxLogObservable
    public Observable<List> getListDynamically(String url, Class domainClass, Class dataClass, boolean persist,
                                               boolean shouldCache) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicList(url, domainClass, dataClass, persist, shouldCache);
    }

    @Override
//    @RxLogObservable
    public Observable<?> getObjectDynamicallyById(String url, String idColumnName, int itemId,
                                                  Class domainClass, Class dataClass,
                                                  boolean persist) {
        if (persist || url.isEmpty())
            return mDataStoreFactory.dynamically(url, idColumnName, itemId, Utils.getDataMapper(dataClass),
                    dataClass).dynamicObject(url, idColumnName, itemId, domainClass, dataClass, true);
        else
            return mDataStoreFactory.cloud(Utils.getDataMapper(dataClass))
                    .dynamicObject(url, idColumnName, itemId, domainClass, dataClass, false);
    }

    @Override
//    @RxLogObservable
    public Observable<?> getObjectDynamicallyById(String url, String idColumnName, int itemId,
                                                  Class domainClass, Class dataClass,
                                                  boolean persist, boolean shouldCache) {
        if (persist || url.isEmpty())
            return mDataStoreFactory.dynamically(url, idColumnName, itemId, Utils.getDataMapper(dataClass),
                    dataClass).dynamicObject(url, idColumnName, itemId, domainClass, dataClass, true, shouldCache);
        else
            return mDataStoreFactory.cloud(Utils.getDataMapper(dataClass))
                    .dynamicObject(url, idColumnName, itemId, domainClass, dataClass, false, shouldCache);
    }

    @Override
//    @RxLogObservable
    public Observable<?> postObjectDynamically(String url, HashMap<String, Object> keyValuePairs,
                                               Class domainClass, Class dataClass, boolean persist) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        if (persist)
            return Observable.concat(mDataStoreFactory
                            .disk(mEntityDataMapper)
                            .dynamicPostObject(url, keyValuePairs, domainClass, dataClass, persist),
                    mDataStoreFactory.cloud(mEntityDataMapper).dynamicPostObject(url,
                            keyValuePairs, domainClass, dataClass, true))
                    .distinct();
        return mDataStoreFactory.cloud(mEntityDataMapper).dynamicPostObject(url, keyValuePairs,
                domainClass, dataClass, false);
    }

    @Override
    public Observable<?> postObjectDynamically(String url, JSONObject keyValuePairs, Class domainClass,
                                               Class dataClass, boolean persist) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        if (persist)
            return Observable.concat(mDataStoreFactory.disk(mEntityDataMapper)
                            .dynamicPostObject(url, keyValuePairs, domainClass, dataClass, persist),
                    mDataStoreFactory.cloud(mEntityDataMapper).dynamicPostObject(url,
                            keyValuePairs, domainClass, dataClass, true))
                    .distinct();
        return mDataStoreFactory.cloud(mEntityDataMapper).dynamicPostObject(url, keyValuePairs,
                domainClass, dataClass, false);
    }

    @Override
//    @RxLogObservable
    public Observable<List> postListDynamically(String url, HashMap<String, Object> keyValuePairs,
                                                Class domainClass, Class dataClass, boolean persist) {
        return mDataStoreFactory.cloud(Utils.getDataMapper(dataClass)).dynamicPostList(url, keyValuePairs,
                domainClass, dataClass, persist);
    }

    @Override
//    @RxLogObservable
    public Observable<?> deleteListDynamically(String url, HashMap<String, Object> keyValuePairs,
                                               Class domainClass, Class dataClass, boolean persist) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        if (persist)
            return Observable.concat(mDataStoreFactory
                            .disk(mEntityDataMapper)
                            .dynamicDeleteCollection(url, keyValuePairs, dataClass, persist),
                    mDataStoreFactory.cloud(mEntityDataMapper).dynamicDeleteCollection(url,
                            keyValuePairs, dataClass, true))
                    .distinct();
        return mDataStoreFactory.cloud(mEntityDataMapper).dynamicPostObject(url, keyValuePairs,
                domainClass, dataClass, false);
    }

    @Override
//    @RxLogObservable
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mDataStoreFactory.disk(Utils.getDataMapper(dataClass)).searchDisk(query, column,
                domainClass, dataClass);
    }

    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return mDataStoreFactory.disk(Utils.getDataMapper(domainClass)).searchDisk(query, domainClass);
    }

    @Override
    public Observable<?> putObjectDynamically(String url, HashMap<String, Object> keyValuePairs, Class domainClass,
                                              Class dataClass, boolean persist) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        if (persist)
            return Observable.concat(mDataStoreFactory
                            .disk(mEntityDataMapper)
                            .dynamicPutObject(url, keyValuePairs, domainClass, dataClass, persist),
                    mDataStoreFactory.cloud(mEntityDataMapper).dynamicPutObject(url,
                            keyValuePairs, domainClass, dataClass, true))
                    .distinct();
        return mDataStoreFactory.cloud(mEntityDataMapper).dynamicPutObject(url, keyValuePairs,
                domainClass, dataClass, false);
    }

    @Override
    public Observable<List> putListDynamically(String url, HashMap<String, Object> keyValuePairs,
                                               Class domainClass, Class dataClass, boolean persist) {
        return mDataStoreFactory.cloud(Utils.getDataMapper(dataClass)).dynamicPutList(url, keyValuePairs,
                domainClass, dataClass, persist);
    }

    @Override
    public Observable<?> deleteAllDynamically(String url, Class dataClass, boolean persist) {
        mEntityDataMapper = Utils.getDataMapper(dataClass);
        if (persist && !url.isEmpty())
            return Observable.concat(mDataStoreFactory
                            .disk(mEntityDataMapper)
                            .dynamicDeleteAll(url, dataClass, true),
                    mDataStoreFactory.cloud(mEntityDataMapper).dynamicDeleteAll(url, dataClass, true))
                    .distinct();
        if (url.isEmpty())
            return mDataStoreFactory.disk(mEntityDataMapper).dynamicDeleteAll(url, dataClass, true);
        return mDataStoreFactory.cloud(mEntityDataMapper).dynamicDeleteAll(url, dataClass, false);
    }
}