package com.zeyad.cleanarchitecturet.data.repository.datasource.generalstore;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.db.RealmRepository;
import com.zeyad.cleanarchitecturet.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecturet.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecturet.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

import rx.Observable;

public class DiskDataStore implements DataStore {

    private final GeneralRealmManager realmManager;
    private RealmRepository realmRepository;
    private final String TAG = "DiskUserDataStore";

    /**
     * Construct a {@link UserDataStore} based file system data store.
     *
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public DiskDataStore(GeneralRealmManager realmManager) {
        this.realmManager = realmManager;
    }

    @Override
    public Observable<Collection> entityListFromDisk(Class clazz) {
        return realmManager.getAll(clazz).compose(Utils.logSources(TAG, realmManager));
    }

    @Override
    public Observable<?> entityDetailsFromDisk(final int itemId, Class clazz) {
        return realmManager.get(itemId, clazz).compose(Utils.logSource(TAG, realmManager));
    }

    @Override
    public Observable<Collection> collectionFromCloud(Class clazz) {
        try {
            throw new Exception("cant get from cloud in disk data store");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<?> entityDetailsFromCloud(int itemId, Class clazz) {
        try {
            throw new Exception("cant get from cloud in disk data store");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //--------------------------------------------------------------------------------------------//

    public Observable store(Class clazz, Object object) {
        try {
            realmRepository.storeObject(clazz, new JSONObject(new Gson().toJson(object, clazz)));
            return Observable.empty();
        } catch (JSONException e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }

    public Observable<?> getById(Class clazz, String column, final int id) {
        return realmRepository.get(clazz, predicate -> predicate.equalTo(column, id));
    }
}