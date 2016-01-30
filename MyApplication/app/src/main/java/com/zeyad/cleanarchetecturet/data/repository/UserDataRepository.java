package com.zeyad.cleanarchetecturet.data.repository;

import com.zeyad.cleanarchetecturet.data.entity.UserEntity;
import com.zeyad.cleanarchetecturet.data.entity.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchetecturet.data.exception.NetworkConnectionException;
import com.zeyad.cleanarchetecturet.data.repository.datasource.UserDataStore;
import com.zeyad.cleanarchetecturet.data.repository.datasource.UserDataStoreFactory;
import com.zeyad.cleanarchetecturet.domain.User;
import com.zeyad.cleanarchetecturet.domain.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * {@link UserRepository} for retrieving user data.
 */
@Singleton
public class UserDataRepository implements UserRepository {

    private final UserDataStoreFactory userDataStoreFactory;
    private final UserEntityDataMapper userEntityDataMapper;

    /**
     * Constructs a {@link UserRepository}.
     *
     * @param dataStoreFactory     A factory to construct different data source implementations.
     * @param userEntityDataMapper {@link UserEntityDataMapper}.
     */
    @Inject
    public UserDataRepository(UserDataStoreFactory dataStoreFactory,
                              UserEntityDataMapper userEntityDataMapper) {
        this.userDataStoreFactory = dataStoreFactory;
        this.userEntityDataMapper = userEntityDataMapper;
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<List<User>> users() {
        //we always get all users from the cloud
        final UserDataStore userDataStore = userDataStoreFactory.createCloudDataStore();
        return userDataStore.userEntityList().map(new Func1<List<UserEntity>, List<User>>() {
            @Override
            public List<User> call(List<UserEntity> roomEntities) {
                return userEntityDataMapper.transform(roomEntities);
            }
        });
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<User> user(int userId) {
        try {
            final UserDataStore userDataStore = this.userDataStoreFactory.create(userId);
            return userDataStore.userEntityDetails(userId).map(new Func1<UserEntity, User>() {
                @Override
                public User call(UserEntity roomEntity) {
                    return userEntityDataMapper.transform(roomEntity);
                }
            });
        } catch (NetworkConnectionException e) {
            e.printStackTrace();
            return null;
        }
    }

//    @SuppressWarnings("Convert2MethodRef")
//    @Override
//    public Observable<User> user(int userId) {
//        final UserDataStore userDataStore = this.userDataStoreFactory.create(userId);
//        return userDataStore.userEntityDetails(userId).map(new Func1<UserEntity, User>() {
//            @Override
//            public User call(UserEntity roomEntity) {
//                return userEntityDataMapper.transform(roomEntity);
//            }
//        });
//    }
}