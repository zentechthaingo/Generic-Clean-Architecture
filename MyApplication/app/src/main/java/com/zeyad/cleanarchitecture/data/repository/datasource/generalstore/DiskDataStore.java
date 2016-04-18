package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.Collection;

import io.realm.RealmObject;
import rx.Observable;

public class DiskDataStore implements DataStore {

    private GeneralRealmManager mRealmManager;
    private UserEntityDataMapper mEntityDataMapper;
    public final String TAG = "DiskUserDataStore";

    /**
     * Construct a {@link UserDataStore} based file system data store.
     *
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public DiskDataStore(GeneralRealmManager realmManager, EntityDataMapper entityDataMapper) {
        mRealmManager = realmManager;
        mEntityDataMapper = new UserEntityDataMapper();
    }

    @Override
    public Observable<Collection> collection(Class domainClass, Class dataClass) {
        return mRealmManager.getAll(dataClass)
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels))
                .compose(Utils.logSources(TAG, mRealmManager));
    }

    @Override
    public Observable<?> entityDetails(final int itemId, Class domainClass, Class dataClass) {
        return mRealmManager.getById(itemId, dataClass)
                .map(realmModel -> mEntityDataMapper.transformToDomain((UserRealmModel) realmModel))
                .compose(Utils.logSource(TAG, mRealmManager));
    }

    @Override
    public Observable<Collection> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return mRealmManager.getWhere(dataClass, predicate -> predicate.equalTo(column, query))
                .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel));
    }

    @Override
    public Observable<?> putToDisk(RealmObject object) {
        return Observable.defer(() -> mRealmManager.put(object))
                .map(realmModel -> mEntityDataMapper.transformToDomain((UserRealmModel) realmModel));
    }

    @Override
    public Observable<?> deleteCollectionFromDisk(Collection<Integer> collection, Class clazz) {
        return Observable.defer(() -> mRealmManager.evictCollection(collection, clazz));
    }

    @Override
    public Observable<Collection> searchCloud(String query, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant getById from cloud in disk data store"));
    }

    @Override
    public Observable<?> postToCloud(Object object, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant getById from cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteCollectionFromCloud(Collection collection, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant getById from cloud in disk data store"));
    }
}