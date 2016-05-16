package com.zeyad.cleanarchitecture.domain.repository;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
// TODO: 13/05/16 Document!

/**
 * Interface that represents a Repository for getting Objects from the data layer.
 */
public interface Repository {
    /**
     * Get an {@link Observable} which will emit a collectionFromDisk of Items.
     */
    Observable<List> dynamicList(String url, Class domainClass, Class dataClass, boolean persist);


    /**
     * Get an {@link Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve getDynamicallyById data.
     */
    Observable<?> getObjectDynamicallyById(String url, int itemId, Class domainClass, Class dataClass,
                                           boolean persist);

    Observable<?> postObjectDynamically(String url, HashMap<String, Object> keyValuePairs,
                                        Class domainClass, Class dataClass, boolean persist);

    Observable<List> postListDynamically(String url, HashMap<String, Object> keyValuePairs,
                                         Class domainClass, Class dataClass, boolean persist);

    Observable<?> deleteListDynamically(String url, HashMap<String, Object> keyValuePairs, Class domainClass,
                                        Class dataClass, boolean persist);

    Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass);
}