package com.zeyad.cleanarchitecture.data.repository.datastore;

import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface DataStore {

    String IDS = "ids";

    /**
     * Get an {@link rx.Observable} which will emit a collectionFromDisk of ?.
     */
    Observable<List> dynamicList(final String url, Class domainClass, Class dataClass, boolean persist);

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     */
    Observable<?> dynamicObject(final String url, final String idColumnName, final int itemId,
                                Class domainClass, Class dataClass, boolean persist);

    Observable<?> dynamicPostObject(final String url, final HashMap<String, Object> keyValuePairs,
                                    Class domainClass, Class dataClass, boolean persist);

    Observable<List> dynamicPostList(final String url, final HashMap<String, Object> keyValuePairs,
                                     Class domainClass, Class dataClass, boolean persist);

    Observable<?> putToDisk(HashMap<String, Object> object, Class dataClass);


    Observable<?> deleteCollectionFromDisk(final HashMap<String, Object> keyValuePairs, Class dataClass);

    Observable<?> deleteCollectionFromCloud(final String url, final HashMap<String, Object> keyValuePairs,
                                            Class dataClass, boolean persist);

    Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass);

    Observable<List> searchDisk(RealmQuery query, Class domainClass);
}