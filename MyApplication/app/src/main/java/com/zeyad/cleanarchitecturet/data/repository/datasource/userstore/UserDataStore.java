package com.zeyad.cleanarchitecturet.data.repository.datasource.userstore;

import com.zeyad.cleanarchitecturet.data.entities.UserEntity;

import rx.Observable;

/**
 * Interface that represents a data store from where data is retrieved.
 */
public interface UserDataStore {
    /**
     * Get an {@link rx.Observable} which will emit a List of {@link UserEntity}.
     */
    Observable userEntityList();

    /**
     * Get an {@link rx.Observable} which will emit a {@link UserEntity} by its id.
     *
     * @param itemId The id to retrieve user data.
     */
    Observable<UserEntity> userEntityDetails(final int itemId);
}