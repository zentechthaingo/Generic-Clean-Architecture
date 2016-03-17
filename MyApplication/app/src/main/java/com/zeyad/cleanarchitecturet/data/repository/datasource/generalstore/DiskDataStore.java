package com.zeyad.cleanarchitecturet.data.repository.datasource.generalstore;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.db.RealmRepository;
import com.zeyad.cleanarchitecturet.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecturet.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecturet.data.repository.datasource.userstore.UserDataStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;

public class DiskDataStore<T> implements DataStore<T> {

    private final GeneralRealmManager realmManager;
    private RealmRepository realmRepository;
    private final EntityDataMapper entityDataMapper;
    private final String TAG = "DiskUserDataStore";

    /**
     * Construct a {@link UserDataStore} based file system data store.
     *
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public DiskDataStore(GeneralRealmManager realmManager,
                         EntityDataMapper entityDataMapper) {
        this.realmManager = realmManager;
        this.entityDataMapper = entityDataMapper;
    }

    // FIXME: 3/12/16 Generalize data mapper!
    @Override
    public Observable<List<?>> entityListFromDisk(Class clazz) {
        return realmManager.getAll(clazz).map(realmObjects -> entityDataMapper.transformAll(realmObjects));
//                .compose(Utils.logUsersSource(TAG, realmManager));
    }

    // FIXME: 3/12/16 Generalize data mapper!
    @Override
    public Observable<?> entityDetailsFromDisk(final int itemId, Class clazz) {
        return realmManager.get(itemId, clazz).map(realmObject -> entityDataMapper.transform(realmObject));
//                .compose(Utils.logUserSource(TAG, realmManager));
    }

    @Override
    public Observable<List<?>> entityListFromCloud() {
        try {
            throw new Exception("cant get from cloud in disk data store");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<?> entityDetailsFromCloud(int itemId) {
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