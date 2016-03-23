package com.zeyad.cleanarchitecturet.data.repository;

import com.zeyad.cleanarchitecturet.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecturet.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecturet.data.repository.datasource.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecturet.domain.repositories.Repository;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmObject;
import rx.Observable;

@Singleton
public class DataRepository implements Repository {

    private final DataStoreFactory dataStoreFactory;
    private final EntityDataMapper entityDataMapper;

    /**
     * Constructs a {@link UserRepository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     * @param entityDataMapper {@link UserEntityDataMapper}.
     */
    @Inject
    public DataRepository(DataStoreFactory dataStoreFactory, EntityDataMapper entityDataMapper) {
        this.dataStoreFactory = dataStoreFactory;
        this.entityDataMapper = entityDataMapper;
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<Collection> Collection(Class clazz) {
        return dataStoreFactory.createAll(entityDataMapper, clazz)
                .collectionFromCloud(clazz);
//                .map(realmModels -> entityDataMapper.transformAllToDomain(realmModels, clazz));
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getAllUsersFromAllSources(userDataStoreFactory
//                .createAllFromCloud(entityDataMapper)
//                .userEntityList(), userDataStoreFactory.createAllFromDisk(entityDataMapper)
//                .userEntityList()).map(userEntities -> entityDataMapper.transformToDomain(userEntities));
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<?> item(int itemId, Class clazz) {
        return dataStoreFactory.createById(itemId, entityDataMapper, clazz)
                .entityDetailsFromCloud(itemId, clazz);
//                .map(entityDataMapper::transform);
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getUserFromAllSources(userDataStoreFactory
//                .createByIdFromCloud(entityDataMapper)
//                .userEntityDetails(userId), userDataStoreFactory.createByIdFromDisk(entityDataMapper)
//                .userEntityDetails(userId)).map(userEntity -> entityDataMapper.transformToDomain(userEntity));
    }

    @Override
    public Observable<?> putAll(int itemId, Class clazz) {
        return null;
    }

    @Override
    public Observable<?> put(int itemId, Class clazz) {
        return null;
    }

    @Override
    public Observable<?> delete(int itemId, Class clazz) {
        return null;
    }

    @Override
    public Observable<?> delete(RealmObject realmObject, Class clazz) {
        return null;
    }

    @Override
    public Observable<?> evictAll(Class clazz) {
        return null;
    }
}
