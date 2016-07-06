package com.zeyad.cleanarchitecture.data.repository.generalstore;

import org.json.JSONObject;

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

    Observable<List> dynamicList(final String url, Class domainClass, Class dataClass, boolean persist,
                                 boolean shouldCache);

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     */
    Observable<?> dynamicObject(final String url, final String idColumnName, final int itemId,
                                Class domainClass, Class dataClass, boolean persist);

    /**
     * Get an {@link rx.Observable} which will emit a ? by its id.
     */
    Observable<?> dynamicObject(final String url, final String idColumnName, final int itemId,
                                Class domainClass, Class dataClass, boolean persist, boolean shouldCache);

    /**
     * Post a HashMap<String, Object> which returns an {@link rx.Observable} that will emit a ?.
     */
    Observable<?> dynamicPostObject(final String url, final HashMap<String, Object> keyValuePairs,
                                    Class domainClass, Class dataClass, boolean persist);

    /**
     * Post a JSONObject which returns an {@link rx.Observable} that will emit a ?.
     */
    Observable<?> dynamicPostObject(final String url, final JSONObject keyValuePairs, Class domainClass,
                                    Class dataClass, boolean persist);

    /**
     * Post a HashMap<String, Object> which returns an {@link rx.Observable} that will emit a list of ?.
     */
    Observable<List> dynamicPostList(final String url, final HashMap<String, Object> keyValuePairs,
                                     Class domainClass, Class dataClass, boolean persist);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link rx.Observable}
     * that will emit a ?.
     */
    Observable<?> dynamicPutObject(final String url, final HashMap<String, Object> keyValuePairs,
                                   Class domainClass, Class dataClass, boolean persist);

    /**
     * Put a HashMap<String, Object> disk with a RealmQuery which returns an {@link rx.Observable}
     * that will emit a list of ?.
     */
    Observable<List> dynamicPutList(final String url, final HashMap<String, Object> keyValuePairs,
                                    Class domainClass, Class dataClass, boolean persist);

    /**
     * Delete a HashMap<String, Object> from cloud which returns an {@link rx.Observable} that will emit a ?.
     */
    Observable<?> dynamicDeleteCollection(final String url, final HashMap<String, Object> keyValuePairs,
                                          Class dataClass, boolean persist);

    /**
     * Delete all items of the same type from cloud or disk which returns an {@link rx.Observable}
     * that will emit a list of ?.
     */
    Observable<?> dynamicDeleteAll(String url, Class dataClass, boolean persist);

    /**
     * Search disk with a query which returns an {@link rx.Observable} that will emit a list of ?.
     */
    Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass);

    /**
     * Search disk with a RealmQuery which returns an {@link rx.Observable} that will emit a list of ?.
     */
    Observable<List> searchDisk(RealmQuery query, Class domainClass);
}