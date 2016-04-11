package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import java.util.Collection;

import io.realm.RealmObject;
import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {
    /**
     * Get an {@link rx.Observable} which will emit a Collection of ?.
     */
    Observable<Collection> collection(Class domainClass, Class dataClass);

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     *
     * @param itemId The id to retrieve user data.
     */
    Observable<?> entityDetails(final int itemId, Class domainClass, Class dataClass);

    Observable<?> postToCloud(Object object, Class domainClass, Class dataClass);

    Observable<?> putToDisk(RealmObject object);

    Observable<?> deleteCollectionFromCloud(final Collection collection, Class domainClass, Class dataClass);

    Observable<?> deleteCollectionFromDisk(final Collection<Integer> collection, Class clazz);

    Observable<Collection> searchCloud(String query, Class domainClass, Class dataClass);

    Observable<Collection> searchDisk(String query, String column, Class domainClass, Class dataClass);
}