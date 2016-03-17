package com.zeyad.cleanarchitecturet.data.repository.datasource.generalstore;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecturet.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecturet.data.network.RestApi;
import com.zeyad.cleanarchitecturet.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecturet.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action1;

public class CloudDataStore<R> implements DataStore<R> {

    private final RestApi restApi;
    private GeneralRealmManager realmManager;
    private final EntityDataMapper entityDataMapper;
    private final String TAG = "CloudDataStore";
    private final Action1<R> saveToCacheAction = realmModel -> {
        try {
            realmManager.put(new JSONObject(new Gson().toJson(realmModel)), realmModel.getClass());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };
    private final Action1<List<?>> saveAllToCacheAction = realmModels -> {
        try {
            realmManager.putAll(new JSONArray(new Gson().toJson(realmModels)), realmModels.getClass());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public Observable<List<?>> entityListFromDisk(Class clazz) {
        try {
            throw new Exception("cant get from disk in cloud data store");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<List<?>> entityListFromCloud() {
        return restApi.userList()
                .doOnNext(saveAllToCacheAction)
                .map(userRealmModels -> entityDataMapper.transformAll((Collection) userRealmModels));
//                .compose(Utils.logUsersSource(TAG, realmManager));
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
    public Observable<?> entityDetailsFromCloud(final int itemId) {
        return restApi.userById(itemId)
                .doOnNext(realmObject -> saveToCacheAction.call((R) realmObject))
                .map(userRealmModel -> entityDataMapper.transform((RealmObject) userRealmModel));
//                .compose(Utils.logUserSource(TAG, realmManager));
    }
}