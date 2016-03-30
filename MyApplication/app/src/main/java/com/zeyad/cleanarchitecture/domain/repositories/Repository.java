package com.zeyad.cleanarchitecture.domain.repositories;

import com.zeyad.cleanarchitecture.domain.models.User;

import java.util.Collection;

import rx.Observable;

/**
 * Interface that represents a Repository for getting {@link User} related data.
 */
public interface Repository {
    /**
     * Get an {@link rx.Observable} which will emit a Collection of Items.
     */
    Observable<Collection> Collection(Class presentationClass, Class domainClass, Class dataClass);

    /**
     * Get an {@link rx.Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve getById data.
     */
    Observable<?> getById(final int itemId, Class presentationClass, Class domainClass, Class dataClass);

    Observable<?> put(final Object object, Class domainClass, Class dataClass);

    Observable<?> delete(final long itemId, Class clazz);

    Observable<?> delete(final Object realmObject, Class clazz);

    Observable<?> deleteCollection(final Collection collection, Class clazz);

    Observable<?> search();
}