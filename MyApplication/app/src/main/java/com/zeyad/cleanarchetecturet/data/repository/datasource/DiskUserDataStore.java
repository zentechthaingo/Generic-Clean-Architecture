package com.zeyad.cleanarchetecturet.data.repository.datasource;

import com.zeyad.cleanarchetecturet.data.db.RealmManager;
import com.zeyad.cleanarchetecturet.data.entities.UserEntity;
import com.zeyad.cleanarchetecturet.data.entities.UserRealmModel;
import com.zeyad.cleanarchetecturet.data.entities.mapper.UserEntityDataMapper;

import java.util.List;

import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

/**
 * {@link UserDataStore} implementation based on file system data store.
 */
public class DiskUserDataStore implements UserDataStore {

    private final RealmManager realmManager;
    private final UserEntityDataMapper userEntityDataMapper;

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
        return realmManager.getAll().map(new Func1<RealmResults<UserRealmModel>, List<UserEntity>>() {
            @Override
            public List<UserEntity> call(RealmResults<UserRealmModel> userRealmModel) {
                return userEntityDataMapper.transformAll(userRealmModel);
            }
        });
    }

    @Override
    public Observable<UserEntity> userEntityDetails(final int userId) {
        return realmManager.get(userId).map(new Func1<UserRealmModel, UserEntity>() {
            @Override
            public UserEntity call(UserRealmModel userRealmModel) {
                return userEntityDataMapper.transform(userRealmModel);
            }
        });
    }
}