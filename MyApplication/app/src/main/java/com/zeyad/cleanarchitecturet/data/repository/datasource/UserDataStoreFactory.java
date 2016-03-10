package com.zeyad.cleanarchitecturet.data.repository.datasource;

import android.content.Context;

import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecturet.data.network.RestApiImpl;
import com.zeyad.cleanarchitecturet.utilities.Utils;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
// TODO: 3/10/16 Generalize!

/**
 * Factory that creates different implementations of {@link UserDataStore}.
 */
@Singleton
public class UserDataStoreFactory {

    private final RealmManager mRealmManager;
    private final Context mContext;

    @Inject
    public UserDataStoreFactory(RealmManager realmManager, Context context) {
        if (realmManager == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        mContext = context;
        mRealmManager = realmManager;
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore createById(int userId, UserEntityDataMapper userEntityDataMapper) {
        if (mRealmManager.isUserValid(userId))
            return new DiskUserDataStore(mRealmManager, userEntityDataMapper);
        else
            return createCloudDataStore(userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public UserDataStore createAll(UserEntityDataMapper userEntityDataMapper) {
        if (mRealmManager.areUsersValid() || !Utils.isNetworkAvailable(mContext))
            return new DiskUserDataStore(mRealmManager, userEntityDataMapper);
        else return createCloudDataStore(userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud.
     */
    public UserDataStore createCloudDataStore(UserEntityDataMapper userEntityDataMapper) {
        return new CloudUserDataStore(new RestApiImpl(), mRealmManager, userEntityDataMapper);
    }

    //----------------------------------Get Simultaneously----------------------------------------//

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore createByIdFromDisk(UserEntityDataMapper userEntityDataMapper) {
        return new DiskUserDataStore(mRealmManager, userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore createByIdFromCloud(UserEntityDataMapper userEntityDataMapper) {
        return createCloudDataStore(userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public UserDataStore createAllFromDisk(UserEntityDataMapper userEntityDataMapper) {
        return new DiskUserDataStore(mRealmManager, userEntityDataMapper);
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud or DB.
     */
    public UserDataStore createAllFromCloud(UserEntityDataMapper userEntityDataMapper) {
        return createCloudDataStore(userEntityDataMapper);
    }

    public Observable<List<UserEntity>> getAllUsersFromAllSources(Observable<List<UserEntity>> cloud,
                                                                  Observable<List<UserEntity>> disk) {
        return Observable.concat(disk, cloud)
                .first(userEntity -> userEntity != null && mRealmManager.areUsersValid());
    }

    public Observable<UserEntity> getUserFromAllSources(Observable<UserEntity> cloud,
                                                        Observable<UserEntity> disk) {
        return Observable.concat(disk, cloud)
                .first(userEntity -> userEntity != null && mRealmManager.areUsersValid());
    }
}