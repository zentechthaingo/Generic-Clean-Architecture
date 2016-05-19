package com.zeyad.cleanarchitecture.data.repository.datastore;

import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import rx.Observable;

public class DiskDataStore implements DataStore {

    private GeneralRealmManager mRealmManager;
    private EntityMapper mEntityDataMapper;
    public final String TAG = DiskDataStore.class.getName();

    /**
     * Construct a {@link DataStore} based file system data store.
     *
     * @param realmManager A {@link GeneralRealmManager} to cache data retrieved from the api.
     */
    public DiskDataStore(GeneralRealmManager realmManager, EntityMapper entityDataMapper) {
        mRealmManager = realmManager;
        mEntityDataMapper = entityDataMapper;
    }

    @Override
    public Observable<List> dynamicList(String url, Class domainClass, Class dataClass, boolean persist) {
        return mRealmManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels, domainClass))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @Override
    public Observable<?> dynamicObject(String url, String idColumnName, int itemId, Class domainClass,
                                       Class dataClass, boolean persist) {
        return mRealmManager.getById(idColumnName, itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        //  .compose(Utils.logSource(TAG, mRealmManager));
    }

    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mRealmManager.getWhere(dataClass, query, column)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @Override
    public Observable<?> putToDisk(HashMap<String, Object> object, Class dataClass) {
        return Observable.defer(() -> mRealmManager.put(new JSONObject(object), dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @Override
    public Observable<?> deleteCollectionFromDisk(HashMap<String, Object> keyValuePairs, Class dataClass) {
        return Observable.defer(() -> mRealmManager.evictCollection((List<Integer>) keyValuePairs.get("ids"),
                dataClass));
    }

    @Override
    public Observable<?> deleteCollectionFromCloud(String url, HashMap<String, Object> keyValuePairs,
                                                   Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant get Object from cloud in disk data store"));
    }


    @Override
    public Observable<?> dynamicPostObject(String url, HashMap<String, Object> keyValuePairs,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant post to cloud in disk data store"));
    }

    @Override
    public Observable<List> dynamicPostList(String url, HashMap<String, Object> keyValuePairs,
                                            Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant post to cloud in disk data store"));
    }
}