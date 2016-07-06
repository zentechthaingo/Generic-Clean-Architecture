package com.zeyad.cleanarchitecture.domain.repository;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;

/**
 * Interface that represents a Repository for getting Objects from the data layer.
 */
// TODO: 13/05/16 Document!
public interface Repository {
    /**
     * Get an {@link Observable} which will emit a collectionFromDisk of Items.
     */
    Observable<List> getListDynamically(String url, Class domainClass, Class dataClass, boolean persist);

    /**
     * Get an {@link Observable} which will emit a collectionFromDisk of Items.
     */
    Observable<List> getListDynamically(String url, Class domainClass, Class dataClass, boolean persist,
                                        boolean shouldCache);

    /**
     * Get an {@link Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve getDynamicallyById data.
     */
    Observable<?> getObjectDynamicallyById(String url, String idColumnName, int itemId, Class domainClass,
                                           Class dataClass, boolean persist);

    /**
     * Get an {@link Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve getDynamicallyById data.
     */
    Observable<?> getObjectDynamicallyById(String url, String idColumnName, int itemId, Class domainClass,
                                           Class dataClass, boolean persist, boolean shouldCache);

    Observable<?> postObjectDynamically(String url, HashMap<String, Object> keyValuePairs,
                                        Class domainClass, Class dataClass, boolean persist);

    Observable<?> postObjectDynamically(String url, JSONObject keyValuePairs, Class domainClass,
                                        Class dataClass, boolean persist);

    Observable<List> postListDynamically(String url, HashMap<String, Object> keyValuePairs,
                                         Class domainClass, Class dataClass, boolean persist);

    Observable<?> deleteListDynamically(String url, HashMap<String, Object> keyValuePairs, Class domainClass,
                                        Class dataClass, boolean persist);

    Observable<?> putObjectDynamically(String url, HashMap<String, Object> keyValuePairs,
                                       Class domainClass, Class dataClass, boolean persist);

    Observable<List> putListDynamically(String url, HashMap<String, Object> keyValuePairs,
                                        Class domainClass, Class dataClass, boolean persist);

    Observable<?> deleteAllDynamically(String url, Class dataClass, boolean persist);

    Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass);

    Observable<List> searchDisk(RealmQuery query, Class domainClass);
}