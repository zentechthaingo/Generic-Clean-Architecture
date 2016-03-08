package com.zeyad.cleanarchitecturet.data.repository;

import com.zeyad.cleanarchitecturet.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecturet.data.repository.datasource.UserDataStoreFactory;
import com.zeyad.cleanarchitecturet.domain.User;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

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
        userDataStoreFactory = dataStoreFactory;
        this.userEntityDataMapper = userEntityDataMapper;
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<List<User>> users() {
        return userDataStoreFactory.createAll(userEntityDataMapper)
                .userEntityList()
                .map(roomEntities -> userEntityDataMapper.transform(roomEntities));
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getAllUsersFromAllSources(userDataStoreFactory
//                .createAllFromCloud(userEntityDataMapper)
//                .userEntityList(), userDataStoreFactory.createAllFromDisk(userEntityDataMapper)
//                .userEntityList()).map(userEntities -> userEntityDataMapper.transform(userEntities));
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<User> user(int userId) {
        return userDataStoreFactory.createById(userId, userEntityDataMapper)
                .userEntityDetails(userId)
                .map(roomEntity -> userEntityDataMapper.transform(roomEntity));
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getUserFromAllSources(userDataStoreFactory
//                .createByIdFromCloud(userEntityDataMapper)
//                .userEntityDetails(userId), userDataStoreFactory.createByIdFromDisk(userEntityDataMapper)
//                .userEntityDetails(userId)).map(userEntity -> userEntityDataMapper.transform(userEntity));
    }
}