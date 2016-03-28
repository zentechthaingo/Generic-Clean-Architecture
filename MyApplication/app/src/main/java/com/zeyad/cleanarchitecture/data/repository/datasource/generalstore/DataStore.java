package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import java.util.Collection;

import rx.Observable;
// TODO: 3/20/16 Add all crud operations!

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {
    /**
     * Get an {@link rx.Observable} which will emit a Collection of ?.
     */
    Observable<Collection> entityListFromDisk(Class clazz);

    Observable<Collection> collectionFromCloud(Class domainClass, Class dataClass);

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     *
     * @param itemId The id to retrieve user data.
     */
    Observable<?> entityDetailsFromDisk(final int itemId, Class clazz);

    Observable<?> entityDetailsFromCloud(final int itemId, Class domainClass, Class dataClass);
}