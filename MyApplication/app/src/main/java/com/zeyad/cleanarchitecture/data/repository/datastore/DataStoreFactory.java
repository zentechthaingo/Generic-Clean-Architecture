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
     * Create {@link DataStore} .
     */
    public DataStore dynamically(String url, EntityMapper entityDataMapper) {
        if (url.isEmpty())
            return new DiskDataStore(mRealmManager, entityDataMapper);
        if (mRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE)
                || !Utils.isNetworkAvailable(mContext))
            return new DiskDataStore(mRealmManager, entityDataMapper);
        else
            return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    /**
     * Create {@link DataStore} from an id.
     */
    public DataStore dynamically(String url, int id, EntityMapper entityDataMapper, Class dataClass) {
        if (url.isEmpty())
            return new DiskDataStore(mRealmManager, entityDataMapper);
        if (mRealmManager.isItemValid(id, dataClass) || !Utils.isNetworkAvailable(mContext))
            return new DiskDataStore(mRealmManager, entityDataMapper);
        else
            return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    public DataStore disk(EntityMapper entityDataMapper) {
        return new DiskDataStore(mRealmManager, entityDataMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    public DataStore cloud(EntityMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mRealmManager, entityDataMapper);
    }
}