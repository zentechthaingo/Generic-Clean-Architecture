package com.zeyad.cleanarchitecture.data.repository.datastore;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.network.RestApiImpl;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import javax.inject.Inject;

public class DataStoreFactory {

    private GeneralRealmManager mRealmManager;
    private final Context mContext;

    @Inject
    public DataStoreFactory(GeneralRealmManager realmManager, Context context) {
        if (realmManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!");
        mContext = context;
        mRealmManager = realmManager;
    }

    /**
     * Create {@link DataStore} from an id.
     */
    public DataStore getById(int id, EntityMapper entityDataMapper, Class dataClass) {
        if (mRealmManager.isItemValid(id, dataClass) || !Utils.isNetworkAvailable(mContext))
            return new DiskDataStore(mRealmManager, entityDataMapper);
        else
            return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    /**
     * Create {@link DataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore getAll(EntityMapper entityDataMapper) {
        if (mRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE)
                || !Utils.isNetworkAvailable(mContext))
            return new DiskDataStore(mRealmManager, entityDataMapper);
        else
            return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    /**
     * Create {@link DataStore} to retrieve data from the Cloud or DB.
     */
    public DataStore getAllDynamicallyFromCloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    /**
     * Create {@link DataStore} from an id.
     */
    public DataStore getObjectDynamicallyFromCloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    public DataStore putToDisk(EntityMapper entityDataMapper) {
        return new DiskDataStore(mRealmManager, entityDataMapper);
    }

    public DataStore putToCloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    public DataStore dynamicPost(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    public DataStore search(EntityMapper entityDataMapper) {
        if (!Utils.isNetworkAvailable(mContext))
            return new DiskDataStore(mRealmManager, entityDataMapper);
        else
            return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    public DataStore deleteCollectionFromCloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    public DataStore deleteCollectionFromDisk(EntityMapper entityDataMapper) {
        return new DiskDataStore(mRealmManager, entityDataMapper);
    }
}