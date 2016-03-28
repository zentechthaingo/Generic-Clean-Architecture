package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.network.RestApi;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.Collection;

import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action1;

public class CloudDataStore implements DataStore {

    private final RestApi restApi;
    private GeneralRealmManager realmManager;
    private EntityDataMapper entityDataMapper;
    private static final String TAG = "CloudDataStore";
    private Class dataClass;
    private final Action1<Object> saveGenericToCacheAction = object -> realmManager.put((RealmObject) entityDataMapper.transformToRealm(object, dataClass));
    private final Action1<Collection> saveAllGenericsToCacheAction = collection -> {
        Collection<RealmObject> realmObjectCollection = new ArrayList<>();
        realmObjectCollection.addAll((Collection) entityDataMapper.transformAllToRealm(collection, dataClass));
        realmManager.putAll(realmObjectCollection);
    };

    /**
     * Construct a {@link UserDataStore} based on connections to the api (Cloud).
     *
     * @param restApi      The {@link RestApi} implementation to use.
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public CloudDataStore(RestApi restApi, GeneralRealmManager realmManager, EntityDataMapper entityDataMapper) {
        this.restApi = restApi;
        this.entityDataMapper = entityDataMapper;
        this.realmManager = realmManager;
    }

    @Override
    public Observable<Collection> entityListFromDisk(Class clazz) {
        try {
            throw new Exception("cant get from disk in cloud data store");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<Collection> collectionFromCloud(Class domainClass, Class dataClass) {
        this.dataClass = dataClass;
        return restApi.userCollection()
//                .retryWhen(observable -> {
//                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                }).repeatWhen(observable -> {
//                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
                .doOnNext(saveAllGenericsToCacheAction)
                .map(realmModels -> entityDataMapper.transformAllToDomain(realmModels, domainClass))
                .compose(Utils.logSources(TAG, realmManager));
    }

    @Override
    public Observable<?> entityDetailsFromDisk(int itemId, Class clazz) {
        try {
            throw new Exception("cant get from disk in cloud data store");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<?> entityDetailsFromCloud(final int itemId, Class domainClass, Class dataClass) {
        this.dataClass = dataClass;
        return restApi.objectById(itemId)
//                .retryWhen(observable -> {
//                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                }).repeatWhen(observable -> {
//                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
                .doOnNext(saveGenericToCacheAction)
                .map(entities -> entityDataMapper.transformToDomain(entities, domainClass));
//                .compose(Utils.logSource(TAG, realmManager));
    }
}