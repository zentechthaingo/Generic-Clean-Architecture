package com.zeyad.cleanarchitecturet.domain.repositories;

import com.zeyad.cleanarchitecturet.domain.models.User;

import java.util.Collection;

import io.realm.RealmObject;
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
     * @param itemId The user id used to retrieve item data.
     */
    Observable<?> item(final int itemId, Class presentationClass, Class domainClass, Class dataClass);

    Observable<?> putAll(final int itemId, Class clazz);

    Observable<?> put(final int itemId, Class clazz);

    Observable<?> delete(final int itemId, Class clazz);

    Observable<?> delete(final RealmObject realmObject, Class clazz);

    Observable<?> evictAll(Class clazz);
}