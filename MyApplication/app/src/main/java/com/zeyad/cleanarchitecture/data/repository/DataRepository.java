package com.zeyad.cleanarchitecture.data.repository;

import com.fernandocejas.frodo.annotation.RxLogObservable;
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
import rx.functions.Func1;

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
//    @RxLogObservable
    public Observable<Collection> Collection(Class presentationClass, Class domainClass, Class dataClass) {
        return dataStoreFactory.getAll(entityDataMapper)
                .collection(domainClass, dataClass);
    }

    //    @SuppressWarnings("Convert2MethodRef")
    @Override
//    @RxLogObservable
    public Observable<?> getById(int itemId, Class presentationClass, Class domainClass, Class dataClass) {
        return dataStoreFactory.getById(itemId, entityDataMapper, dataClass)
                .entityDetails(itemId, domainClass, dataClass);
    }

    @Override
    @RxLogObservable
    public Observable<?> put(Object object, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .putToDisk(entityDataMapper)
                                .putToDisk((RealmObject) entityDataMapper.transformToRealm(object, dataClass)),
                        dataStoreFactory
                                .putToCloud(entityDataMapper)
                                .postToCloud(object, domainClass, dataClass))
                .first();
//                .collect(HashSet::new, HashSet::add)
//                .map(hashSet -> hashSet.iterator().next());
    }

    @Override
    @RxLogObservable
    public Observable<?> deleteCollection(Collection collection, Class presentationClass, Class domainClass, Class dataClass) {
        return Observable
                .merge(dataStoreFactory
                                .deleteCollectionFromCloud(entityDataMapper)
                                .deleteCollectionFromCloud(collection, domainClass, dataClass),
                        dataStoreFactory
                                .deleteCollectionFromDisk(entityDataMapper)
                                .deleteCollectionFromDisk(collection, dataClass))
                .first();
//                .distinct()
////                .collect(HashSet::new, HashSet::add)
//                .flatMap(Observable::from);
    }

    @Override
    @RxLogObservable
    public Observable<?> search(String query, String column, Class presentationClass, Class domainClass, Class dataClass) {
        return dataStoreFactory
                .searchCloud(entityDataMapper)
                .searchCloud(query, domainClass, dataClass)
                .mergeWith(dataStoreFactory
                        .searchDisk(entityDataMapper)
                        .searchDisk(query, column, domainClass, dataClass)
                        .collect(HashSet::new, HashSet::add)
                        .flatMap((Func1<HashSet<Object>, Observable<Collection>>)
                                objects -> Observable.from((Collection) objects)))
                .first();
    }
}