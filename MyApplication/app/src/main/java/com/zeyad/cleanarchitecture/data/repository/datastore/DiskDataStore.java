package com.zeyad.cleanarchitecture.data.repository.datastore;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmObject;
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
    public Observable<List> collection(Class domainClass, Class dataClass, boolean persist) {
        return mRealmManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @Override
    public Observable<?> getById(final int itemId, Class domainClass, Class dataClass, boolean persist) {
        return mRealmManager.getById(itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel))
                .compose(Utils.logSource(TAG, mRealmManager));
    }

    @Override
    public Observable<List> search(String query, String column, Class domainClass, Class dataClass) {
        return mRealmManager.getWhere(dataClass, query, column)
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @Override
    public Observable<?> putToDisk(RealmObject object, Class dataClass) {
        return Observable.defer(() -> mRealmManager.put(object))
                .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @Override
    public Observable<?> putToDisk(Object object, Class dataClass) {
        return Observable.defer(() -> {
            try {
                return mRealmManager.put(new JSONObject(new Gson().toJson(object)), dataClass);
            } catch (JSONException e) {
                e.printStackTrace();
                return Observable.error(e);
            }
        }).map(realmModel -> mEntityDataMapper.transformToDomain(realmModel));
    }

    @Override
    public Observable<?> deleteCollectionFromDisk(List<Integer> list, Class clazz) {
        return Observable.defer(() -> mRealmManager.evictCollection(list, clazz));
    }

    @Override
    public Observable<?> putToCloud(Object object, Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant post to cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteCollectionFromCloud(List list, Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant delete from cloud in disk data store"));
    }


    @Override
    public Observable<List> dynamicList(String url, Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant get List from cloud in disk data store"));
    }

    @Override
    public Observable<?> dynamicObject(String url, Class domainClass, Class dataClass, boolean persist) {
        return Observable.error(new Exception("cant get Object from cloud in disk data store"));
    }
}