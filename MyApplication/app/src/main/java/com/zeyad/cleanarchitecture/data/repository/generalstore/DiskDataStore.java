package com.zeyad.cleanarchitecture.data.repository.generalstore;

import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

// TODO: 10/06/16 reorganize!
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
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @Override
    public Observable<List> dynamicList(String url, Class domainClass, Class dataClass, boolean persist,
                                        boolean shouldCache) {
        return mRealmManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @Override
    public Observable<?> dynamicObject(String url, String idColumnName, int itemId, Class domainClass,
                                       Class dataClass, boolean persist) {
        return mRealmManager.getById(idColumnName, itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
        //  .compose(Utils.logSource(TAG, mRealmManager));
    }

    @Override
    public Observable<?> dynamicObject(String url, String idColumnName, int itemId, Class domainClass,
                                       Class dataClass, boolean persist, boolean shouldCache) {
        return mRealmManager.getById(idColumnName, itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mRealmManager.getWhere(dataClass, query, column)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return mRealmManager.getWhere(query)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @Override
    public Observable<?> dynamicDeleteCollection(String url, HashMap<String, Object> keyValuePairs,
                                                 Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.evictCollection((List<Long>) keyValuePairs.get(DataStore.IDS),
                dataClass));
    }

    @Override
    public Observable<?> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
        return mRealmManager.evictAll(dataClass);
    }

    @Override
    public Observable<?> dynamicPostObject(String url, HashMap<String, Object> keyValuePairs,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(new JSONObject(keyValuePairs), dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @Override
    public Observable<?> dynamicPostObject(String url, JSONObject keyValuePairs, Class domainClass,
                                           Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(keyValuePairs, dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @Override
    public Observable<?> dynamicPutObject(String url, HashMap<String, Object> keyValuePairs,
                                          Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> mRealmManager.put(new JSONObject(keyValuePairs), dataClass))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    // TODO: 10/06/16 implement!
    @Override
    public Observable<List> dynamicPostList(String url, HashMap<String, Object> keyValuePairs,
                                            Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant post to cloud in disk data store"));
    }

    // TODO: 10/06/16 implement!
    @Override
    public Observable<List> dynamicPutList(String url, HashMap<String, Object> keyValuePairs,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant put list to cloud in disk data store"));
    }
}