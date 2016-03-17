package com.zeyad.cleanarchitecturet.data.repository.datasource.generalstore;


import java.util.List;

import rx.Observable;
// TODO: 3/12/16 Generalize to disk and cloud, aka add more methods!

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore<T> {
    /**
     * Get an {@link rx.Observable} which will emit a List of ?.
     */
    Observable<List<?>> entityListFromDisk(Class clazz);

    Observable<List<?>> entityListFromCloud();

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     *
     * @param itemId The id to retrieve user data.
     */
    Observable<?> entityDetailsFromDisk(final int itemId, Class clazz);

    Observable<?> entityDetailsFromCloud(final int itemId);
}