package com.zeyad.cleanarchitecture.data.repository.generalstore;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.data.db.DataBaseManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

public class DiskDataStore implements DataStore {

    private DataBaseManager mRealmManager;
    private EntityMapper mEntityDataMapper;
    public final String TAG = DiskDataStore.class.getName();

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link DataBaseManager} to cache data retrieved from the api.
     */
    public DiskDataStore(DataBaseManager realmManager, EntityMapper entityDataMapper) {
        mRealmManager = realmManager;
        mEntityDataMapper = entityDataMapper;
    }

    @Override
    public Observable<List> dynamicGetList(String url, Class domainClass, Class dataClass, boolean persist) {
        return mRealmManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @Override
    public Observable<List> dynamicGetList(String url, Class domainClass, Class dataClass, boolean persist,
                                           boolean shouldCache) {
        return mRealmManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @NonNull
    @Override
    public Observable<?> dynamicGetObject(String url, String idColumnName, int itemId, Class domainClass,
                                          Class dataClass, boolean persist) {
        return mRealmManager.getById(idColumnName, itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
        //  .compose(Utils.logSource(TAG, mRealmManager));
    }

    @NonNull
    @Override
    public Observable<?> dynamicGetObject(String url, String idColumnName, int itemId, Class domainClass,
                                          Class dataClass, boolean persist, boolean shouldCache) {
        return mRealmManager.getById(idColumnName, itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mRealmManager.getWhere(dataClass, query, column)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return mRealmManager.getWhere(query)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicDeleteCollection(String url, @NonNull HashMap<String, Object> keyValuePairs,
                                                 Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.evictCollection("userId", (List<Long>) keyValuePairs.get(DataStore.IDS),
                dataClass));
    }

    @Override
    public Observable<?> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return mRealmManager.evictAll(dataClass);
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, HashMap<String, Object> keyValuePairs,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(new JSONObject(keyValuePairs), "id", dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostObject(String url, JSONObject keyValuePairs, Class domainClass,
                                           Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(keyValuePairs, "id", dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPutObject(String url, HashMap<String, Object> keyValuePairs,
                                          Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(new JSONObject(keyValuePairs), "id", dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicUploadFile(String url, File file, Class domainClass, Class dataClass,
                                           boolean persist) {
        return Observable.defer(() -> mRealmManager.put(new JSONObject(), "id", dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @NonNull
    @Override
    public Observable<?> dynamicPostList(String url, JSONArray jsonArray, Class domainClass, Class dataClass,
                                         boolean persist) {
        return Observable.defer(() -> mRealmManager.putAll(jsonArray, dataClass));
    }

    // TODO: 10/06/16 implement!
    @NonNull
    @Override
    public Observable<List> dynamicPutList(String url, HashMap<String, Object> keyValuePairs,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant put list to cloud in disk data store"));
    }
}