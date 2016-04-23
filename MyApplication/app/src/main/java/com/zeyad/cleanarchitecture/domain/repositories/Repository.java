package com.zeyad.cleanarchitecture.domain.repositories;

import com.zeyad.cleanarchitecture.domain.models.User;

import java.util.Collection;
import java.util.List;

import rx.Observable;

/**
 * Interface that represents a Repository for getting {@link User} related data.
 */
public interface Repository {
    /**
     * Get an {@link rx.Observable} which will emit a Collection of Items.
     */
    Observable<List> Collection(Class presentationClass, Class domainClass, Class dataClass);

    /**
     * Get an {@link rx.Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve getById data.
     */
    Observable<?> getById(final int itemId, Class presentationClass, Class domainClass, Class dataClass);

    Observable<?> put(final Object object, Class presentationClass, Class domainClass, Class dataClass);

    Observable<Boolean> deleteCollection(final Collection<Integer> collection, Class presentationClass,
                                         Class domainClass, Class dataClass);

    Observable<?> search(String query, String column, Class presentationClass, Class domainClass, Class dataClass);
}