package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.network.RestApiImpl;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;

public class DataStoreFactory {

    private GeneralRealmManager mRealmManager;
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
    public DataStore getById(int id, EntityDataMapper entityDataMapper, Class dataClass) {
//        if (mRealmManager.isItemValid(id, dataClass) || !Utils.isNetworkAvailable(mContext))
//            return new DiskDataStore(mRealmManager);
//        else
        return createCloudDataStore(entityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore getAll(EntityDataMapper entityDataMapper, Class dataClass) {
//        if (mRealmManager.areItemsValid(dataClass) || !Utils.isNetworkAvailable(mContext))
//            return new DiskDataStore(mRealmManager);
//        else
        return createCloudDataStore(entityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud.
     */
    public DataStore createCloudDataStore(EntityDataMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    public DataStore putToDisk() {
        return new DiskDataStore(mRealmManager);
    }

    public DataStore putToCloud(EntityDataMapper entityDataMapper) {
        return createCloudDataStore(entityDataMapper);
    }

    public DataStore searchCloud(EntityDataMapper entityDataMapper) {
        return createOutwardsDataStore(entityDataMapper);
    }

    public DataStore searchDisk() {
        return new DiskDataStore(mRealmManager);
    }

    public DataStore delete(EntityDataMapper entityDataMapper) {
        return createOutwardsDataStore(entityDataMapper);
    }

    public DataStore deleteCollection(EntityDataMapper entityDataMapper) {
        return createOutwardsDataStore(entityDataMapper);
    }

    private DataStore createOutwardsDataStore(EntityDataMapper entityDataMapper) {
        if (Utils.isNetworkAvailable(mContext))
            return createCloudDataStore(entityDataMapper);
        else
            return new DiskDataStore(mRealmManager);
    }

    //----------------------------------Get Simultaneously----------------------------------------//

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public DataStore createByIdFromDisk() {
        return new DiskDataStore(mRealmManager);
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public DataStore createByIdFromCloud(EntityDataMapper entityDataMapper) {
        return createCloudDataStore(entityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore createAllFromDisk() {
        return new DiskDataStore(mRealmManager);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore createAllFromCloud(EntityDataMapper entityDataMapper) {
        return createCloudDataStore(entityDataMapper);
    }

    public Observable<Collection> getAllUsersFromAllSources(Observable<Collection> cloud,
                                                            Observable<Collection> disk) {
        return Observable.concat(disk, cloud)
                .first(userEntity -> userEntity != null && mRealmManager.areItemsValid(null));
    }

    public Observable getUserFromAllSources(Observable cloud, Observable disk) {
        return Observable.concat(disk, cloud)
                .first(userEntity -> userEntity != null && mRealmManager.areItemsValid(null));
    }
}