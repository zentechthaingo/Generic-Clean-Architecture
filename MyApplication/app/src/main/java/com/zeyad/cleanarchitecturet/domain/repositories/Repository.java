package com.zeyad.cleanarchitecturet.domain.repositories;

import com.zeyad.cleanarchitecturet.domain.models.User;

import java.util.List;

import rx.Observable;

/**
 * Interface that represents a Repository for getting {@link User} related data.
 */
public interface Repository<T> {
    /**
     * Get an {@link rx.Observable} which will emit a List of Items.
     */
    Observable<List<?>> list(Class clazz);

    /**
     * Get an {@link rx.Observable} which will emit an Item.
     *
     * @param itemId The user id used to retrieve item data.
     */
    Observable<?> item(final int itemId, Class clazz);

    Observable<?> putAll(final int itemId, Class clazz);

    Observable<?> put(final int itemId, Class clazz);

    Observable<?> delete(final int itemId, Class clazz);

    Observable<?> delete(final T t, Class clazz);

    Observable<?> evictAll(Class clazz);
}