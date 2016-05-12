package com.zeyad.cleanarchitecture.data.repository.datastore;

import java.util.HashMap;
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
    Observable<List> collection(Class domainClass, Class dataClass, boolean persist);

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     *
     * @param itemId The id to retrieve user data.
     */
    Observable<?> getById(final int itemId, Class domainClass, Class dataClass, boolean persist);

    Observable<List> dynamicList(final String url, Class domainClass, Class dataClass, boolean persist);

    Observable<?> dynamicObject(final String url, Class domainClass, Class dataClass, boolean persist);

    Observable<?> dynamicPost(final String url, final HashMap<String, Object> keyValuePairs, Class domainClass);

    Observable<?> putToCloud(Object object, Class domainClass, Class dataClass, boolean persist);

    Observable<?> putToDisk(RealmObject object, Class dataClass);

    Observable<?> putToDisk(Object object, Class dataClass);

    Observable<?> deleteCollectionFromCloud(final List list, Class domainClass, Class dataClass, boolean persist);

    Observable<?> deleteCollectionFromDisk(final List<Integer> list, Class clazz);

    Observable<List> search(String query, String column, Class domainClass, Class dataClass);
}