package com.zeyad.cleanarchitecturet.data.repository.datasource.generalstore;

import android.content.Context;

import com.zeyad.cleanarchitecturet.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecturet.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecturet.data.network.RestApiImpl;
import com.zeyad.cleanarchitecturet.data.repository.datasource.userstore.UserDataStore;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class DataStoreFactory<T> {

    private final GeneralRealmManager mRealmManager;
    private final Context mContext;

    @Inject
    public DataStoreFactory(GeneralRealmManager realmManager, Context context) {
        if (realmManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        mContext = context;
        mRealmManager = realmManager;
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public DataStore<?> createById(int id, EntityDataMapper entityDataMapper) {
//        if (mRealmManager.isItemValid(id, clazz) || !Utils.isNetworkAvailable(mContext))
//            return new DiskDataStore<>(mRealmManager, entityDataMapper);
//        else
        return createCloudDataStore(entityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore<?> createAll(EntityDataMapper entityDataMapper) {
//        if (mRealmManager.areItemsValid() || !Utils.isNetworkAvailable(mContext))
        return new DiskDataStore<>(mRealmManager, entityDataMapper);
//        else return createCloudDataStore(userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud.
     */
    public DataStore<?> createCloudDataStore(EntityDataMapper userEntityDataMapper) {
        return new CloudDataStore<>(new RestApiImpl(), mRealmManager, userEntityDataMapper);
    }

    //----------------------------------Get Simultaneously----------------------------------------//

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public DataStore<?> createByIdFromDisk(EntityDataMapper entityDataMapper) {
        return new DiskDataStore<T>(mRealmManager, entityDataMapper);
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public DataStore<?> createByIdFromCloud(EntityDataMapper entityDataMapper) {
        return createCloudDataStore(entityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore<?> createAllFromDisk(EntityDataMapper entityDataMapper) {
        return new DiskDataStore<T>(mRealmManager, entityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore createAllFromCloud(EntityDataMapper entityDataMapper) {
        return createCloudDataStore(entityDataMapper);
    }

    public Observable<List<?>> getAllUsersFromAllSources(Observable<List<?>> cloud,
                                                         Observable<List<?>> disk) {
        return Observable.concat(disk, cloud)
                .first(userEntity -> userEntity != null && mRealmManager.areItemsValid(null));
    }

    public Observable<?> getUserFromAllSources(Observable<?> cloud,
                                               Observable<?> disk) {
        return Observable.concat(disk, cloud)
                .first(userEntity -> userEntity != null && mRealmManager.areItemsValid(null));
    }
}