package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import java.util.List;

import io.realm.RealmObject;
import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {
    /**
     * Get an {@link rx.Observable} which will emit a collection of ?.
     */
    Observable<List> collection(Class domainClass, Class dataClass);

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     *
     * @param itemId The id to retrieve user data.
     */
    Observable<?> getById(final int itemId, Class domainClass, Class dataClass);

    Observable<?> postToCloud(Object object, Class domainClass, Class dataClass);

    Observable<?> putToDisk(RealmObject object);

    Observable<?> deleteCollectionFromCloud(final List list, Class domainClass, Class dataClass);

    Observable<?> deleteCollectionFromDisk(final List<Integer> list, Class clazz);

    Observable<List> searchCloud(String query, Class domainClass, Class dataClass);

    Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass);
}