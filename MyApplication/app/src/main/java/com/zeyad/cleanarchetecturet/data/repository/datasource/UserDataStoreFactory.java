package com.zeyad.cleanarchetecturet.data.repository.datasource;

import com.zeyad.cleanarchetecturet.data.db.RealmManager;
import com.zeyad.cleanarchetecturet.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchetecturet.data.network.RestApiImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory that creates different implementations of {@link UserDataStore}.
 */
@Singleton
public class UserDataStoreFactory {

    private final RealmManager realmManager;

    @Inject
    public UserDataStoreFactory(RealmManager realmManager) {
        if (realmManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        this.realmManager = realmManager;
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore createById(int userId, UserEntityDataMapper userEntityDataMapper) {
        if (realmManager.isValid(userId))
            return new DiskUserDataStore(realmManager, userEntityDataMapper);
        else
            return createCloudDataStore(userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public UserDataStore createAll(UserEntityDataMapper userEntityDataMapper) {
//        if (realmManager.isValid())
//            return new DiskUserDataStore(realmManager, userEntityDataMapper);
//        else
            return createCloudDataStore(userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud.
     */
    public UserDataStore createCloudDataStore(UserEntityDataMapper userEntityDataMapper) {
        return new CloudUserDataStore(new RestApiImpl(), realmManager, userEntityDataMapper);
    }
}