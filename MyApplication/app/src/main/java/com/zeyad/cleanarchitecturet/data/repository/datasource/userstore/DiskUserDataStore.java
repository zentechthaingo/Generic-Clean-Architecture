package com.zeyad.cleanarchitecturet.data.repository.datasource.userstore;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.db.RealmRepository;
import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.mapper.UserEntityDataMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;

/**
 * {@link UserDataStore} implementation based on file system data store.
 */
public class DiskUserDataStore implements UserDataStore {

    private final RealmManager realmManager;
    private RealmRepository realmRepository;
    private final UserEntityDataMapper userEntityDataMapper;
    private final String TAG = "DiskUserDataStore";

    /**
     * Construct a {@link UserDataStore} based file system data store.
     *
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public DiskUserDataStore(RealmManager realmManager,
                             UserEntityDataMapper userEntityDataMapper) {
        this.realmManager = realmManager;
        this.userEntityDataMapper = userEntityDataMapper;
    }

    @Override
    public Observable<List<UserEntity>> userEntityList() {
        return realmManager.getAll().map(userEntityDataMapper::transformAllFromRealm);
//                .compose(Utils.logSources(TAG, realmManager));
    }

    @Override
    public Observable<UserEntity> userEntityDetails(final int userId) {
        return realmManager.get(userId).map(userEntityDataMapper::transform);
//                .compose(Utils.logSource(TAG, realmManager));
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

    public Observable<UserEntity> getById(Class clazz, String column, final int id) {
        return realmRepository.get(clazz, predicate -> predicate.equalTo(column, id));
    }
}