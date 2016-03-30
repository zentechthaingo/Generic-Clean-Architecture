package com.zeyad.cleanarchitecture.data.repository;

import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;

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
    public Observable<Collection> Collection(Class presentationClass, Class domainClass, Class dataClass) {
        return dataStoreFactory.getAll(entityDataMapper, dataClass)
                .collectionFromCloud(domainClass, dataClass)
                .map(realmModels -> entityDataMapper.transformAllToPresentation(realmModels, presentationClass));
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getAllUsersFromAllSources(userDataStoreFactory
//                .createAllFromCloud(entityDataMapper)
//                .userEntityList(), userDataStoreFactory.createAllFromDisk(entityDataMapper)
//                .userEntityList()).map(userEntities -> entityDataMapper.transformToDomain(userEntities));
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Observable<?> getById(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return dataStoreFactory.getById(itemId, entityDataMapper, dataClass)
                .entityDetailsFromCloud(itemId, domainClass, dataClass)
                .map(realmModel -> entityDataMapper.transformToPresentation(realmModel, presentationClass));
        // TODO: 3/2/16 Test!
//        return userDataStoreFactory.getUserFromAllSources(userDataStoreFactory
//                .createByIdFromCloud(entityDataMapper)
//                .userEntityDetails(userId), userDataStoreFactory.createByIdFromDisk(entityDataMapper)
//                .userEntityDetails(userId)).map(userEntity -> entityDataMapper.transformToDomain(userEntity));
    }

    // TODO: 3/29/16 Add transformations!
    @Override
    public Observable<?> put(Object object, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .put(entityDataMapper)
                                .putToDisk((RealmObject) entityDataMapper.transformToRealm(object, dataClass)),
                        dataStoreFactory
                                .put(entityDataMapper)
                                .postToCloud(object));
    }

    @Override
    public Observable<?> delete(long itemId, Class clazz) {
        return Observable
                .merge(dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromCloud(itemId, clazz),
                        dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromDisk(itemId, clazz));
    }

    // TODO: 3/29/16 Add transformations!
    @Override
    public Observable<?> delete(Object realmObject, Class clazz) {
        return Observable
                .merge(dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromCloud(realmObject, clazz),
                        dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromDisk(realmObject, clazz));
    }

    // TODO: 3/29/16 Add transformations!
    @Override
    public Observable<?> deleteCollection(Collection collection, Class clazz) {
        return Observable
                .merge(dataStoreFactory
                                .deleteCollection(entityDataMapper)
                                .deleteCollectionFromCloud(collection, clazz),
                        dataStoreFactory
                                .deleteCollection(entityDataMapper)
                                .deleteCollectionFromDisk(collection, clazz));
    }

    @Override
    public Observable<?> search() {
        return null;
    }
}
