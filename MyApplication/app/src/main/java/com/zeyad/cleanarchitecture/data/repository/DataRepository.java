package com.zeyad.cleanarchitecture.data.repository;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.data.repository.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.repository.Repository;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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

    /**
     * Constructs a {@link Repository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     */
    @Inject
    public DataRepository(DataStoreFactory dataStoreFactory) {
        mDataStoreFactory = dataStoreFactory;
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
    public Observable<List> getListDynamically(@NonNull String url, Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicGetList(url, domainClass, dataClass, persist);
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
    public Observable<List> getListDynamically(@NonNull String url, Class domainClass, @NonNull Class dataClass, boolean persist,
                                               boolean shouldCache) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicGetList(url, domainClass, dataClass, persist, shouldCache);
    }

    @Override
//    @RxLogObservable
    public Observable<?> getObjectDynamicallyById(@NonNull String url, String idColumnName, int itemId,
                                                  Class domainClass, Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, idColumnName, itemId, Utils.getDataMapper(dataClass),
                dataClass).dynamicGetObject(url, idColumnName, itemId, domainClass, dataClass, persist);
    }

    @Override
//    @RxLogObservable
    public Observable<?> getObjectDynamicallyById(@NonNull String url, String idColumnName, int itemId,
                                                  Class domainClass, Class dataClass, boolean persist,
                                                  boolean shouldCache) {
        return mDataStoreFactory.dynamically(url, idColumnName, itemId, Utils.getDataMapper(dataClass),
                dataClass).dynamicGetObject(url, idColumnName, itemId, domainClass, dataClass, persist,
                shouldCache);
    }

    @Override
//    @RxLogObservable
    public Observable<?> postObjectDynamically(@NonNull String url, HashMap<String, Object> keyValuePairs,
                                               Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicPostObject(url, keyValuePairs, domainClass, dataClass, persist);
    }

    @Override
    public Observable<?> postObjectDynamically(@NonNull String url, JSONObject keyValuePairs, Class domainClass,
                                               @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicPostObject(url, keyValuePairs, domainClass, dataClass, false);
    }

    @Override
//    @RxLogObservable
    public Observable<?> postListDynamically(@NonNull String url, JSONArray jsonArray, Class domainClass,
                                             @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicPostList(url, jsonArray, domainClass, dataClass, persist);
    }

    @Override
//    @RxLogObservable
    public Observable<?> deleteListDynamically(@NonNull String url, HashMap<String, Object> keyValuePairs,
                                               Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicPostObject(url, keyValuePairs, domainClass, dataClass, false);
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
    public Observable<?> uploadFileDynamically(@NonNull String url, File file, Class domainClass, @NonNull Class dataClass,
                                               boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicUploadFile(url, file, domainClass, dataClass, false);
    }

    @Override
    public Observable<?> putObjectDynamically(@NonNull String url, HashMap<String, Object> keyValuePairs,
                                              Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicPutObject(url, keyValuePairs, domainClass, dataClass, false);
    }

    @Override
    public Observable<List> putListDynamically(@NonNull String url, HashMap<String, Object> keyValuePairs,
                                               Class domainClass, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicPutList(url, keyValuePairs, domainClass, dataClass, persist);
    }

    @Override
    public Observable<?> deleteAllDynamically(@NonNull String url, @NonNull Class dataClass, boolean persist) {
        return mDataStoreFactory.dynamically(url, Utils.getDataMapper(dataClass), dataClass)
                .dynamicDeleteAll(url, dataClass, false);
    }
}