package com.zeyad.cleanarchitecturet.data.repository;

import com.zeyad.cleanarchitecturet.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecturet.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecturet.data.repository.datasource.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecturet.domain.repositories.Repository;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by ZIaDo on 3/12/16.
 */
@Singleton
public class DataRepository<T> implements Repository<T> {

    private final DataStoreFactory dataStoreFactory;
    private final EntityDataMapper entityDataMapper;

    /**
     * Constructs a {@link UserRepository}.
     *
     * @param dataStoreFactory A factory to construct different data source implementations.
     * @param entityDataMapper {@link UserEntityDataMapper}.
     */
    @Inject
    public DataRepository(DataStoreFactory dataStoreFactory,
                          EntityDataMapper entityDataMapper) {
        this.dataStoreFactory = dataStoreFactory;
        this.entityDataMapper = entityDataMapper;
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<List<?>> list(Class clazz) {
        return dataStoreFactory.createAll(entityDataMapper)
                .entityListFromDisk(clazz);
//                .map(roomEntities -> entityDataMapper.transform(roomEntities));
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getAllUsersFromAllSources(userDataStoreFactory
//                .createAllFromCloud(entityDataMapper)
//                .userEntityList(), userDataStoreFactory.createAllFromDisk(entityDataMapper)
//                .userEntityList()).map(userEntities -> entityDataMapper.transform(userEntities));
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<T> item(int itemId, Class clazz) {
        return dataStoreFactory.createById(itemId, entityDataMapper)
                .entityDetailsFromDisk(itemId, clazz);
//                .map(roomEntity -> entityDataMapper.transform(roomEntity));
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getUserFromAllSources(userDataStoreFactory
//                .createByIdFromCloud(entityDataMapper)
//                .userEntityDetails(userId), userDataStoreFactory.createByIdFromDisk(entityDataMapper)
//                .userEntityDetails(userId)).map(userEntity -> entityDataMapper.transform(userEntity));
    }

    @Override
    public Observable<T> putAll(int itemId, Class clazz) {
        return null;
    }

    @Override
    public Observable<T> put(int itemId, Class clazz) {
        return null;
    }

    @Override
    public Observable<T> delete(int itemId, Class clazz) {
        return null;
    }

    @Override
    public Observable<T> delete(T t, Class clazz) {
        return null;
    }

    @Override
    public Observable<T> evictAll(Class clazz) {
        return null;
    }
}
