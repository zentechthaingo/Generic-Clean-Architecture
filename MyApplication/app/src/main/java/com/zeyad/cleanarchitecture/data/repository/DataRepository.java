package com.zeyad.cleanarchitecture.data.repository;

import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.domain.repositories.UserRepository;

import java.util.Collection;
import java.util.HashSet;

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
//        return dataStoreFactory.getAllUsersFromAllSources(userDataStoreFactory
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
//        return dataStoreFactory.getUserFromAllSources(userDataStoreFactory
//                .createByIdFromCloud(entityDataMapper)
//                .userEntityDetails(userId), userDataStoreFactory.createByIdFromDisk(entityDataMapper)
//                .userEntityDetails(userId)).map(userEntity -> entityDataMapper.transformToDomain(userEntity));
    }

    @Override
    public Observable<?> put(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .putToDisk()
                                .putToDisk((RealmObject) entityDataMapper.transformToRealm(object, dataClass))
                                .map(object1 -> entityDataMapper.transformToPresentation(object1, presentationClass)),
                        dataStoreFactory
                                .putToCloud(entityDataMapper)
                                .postToCloud(object, domainClass, dataClass)
                                .map(object1 -> entityDataMapper.transformToPresentation(object1, presentationClass)));
    }

    @Override
    public Observable<?> delete(long itemId, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromCloud(itemId, domainClass, dataClass),
                        dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromDisk(itemId, dataClass));
    }

    @Override
    public Observable<?> delete(Object realmObject, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromCloud(realmObject, domainClass, dataClass),
                        dataStoreFactory
                                .delete(entityDataMapper)
                                .deleteFromDisk(realmObject, dataClass));
    }

    @Override
    public Observable<?> deleteCollection(Collection collection, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .deleteCollection(entityDataMapper)
                                .deleteCollectionFromCloud(collection, domainClass, dataClass),
                        dataStoreFactory
                                .deleteCollection(entityDataMapper)
                                .deleteCollectionFromDisk(collection, dataClass));
    }

    @Override
    public Observable<?> search(String query, Class presentationClass, Class domainClass, Class dataClass) {
//        return Observable
//                .merge(dataStoreFactory
//                                .searchCloud(entityDataMapper)
//                                .searchCloud(query, domainClass, dataClass)
//                                .map(object1 -> entityDataMapper.transformToPresentation(object1, presentationClass)),
//                        dataStoreFactory
//                                .searchDisk()
//                                .searchDisk(query, dataClass)
//                                .map(object1 -> entityDataMapper.transformToPresentation(object1, presentationClass)));
        return dataStoreFactory
                .searchCloud(entityDataMapper)
                .searchCloud(query, domainClass, dataClass)
                .map(object1 -> entityDataMapper.transformToPresentation(object1, presentationClass))
                .mergeWith(dataStoreFactory
                        .searchDisk()
                        .searchDisk(query, dataClass)
                        .map(object1 -> entityDataMapper.transformToPresentation(object1, presentationClass)))
                .collect(HashSet::new, HashSet::add)
                .flatMap(Observable::from);
    }
}