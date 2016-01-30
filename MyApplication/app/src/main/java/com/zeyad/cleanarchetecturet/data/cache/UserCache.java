package com.zeyad.cleanarchetecturet.data.cache;

import com.zeyad.cleanarchetecturet.data.entity.UserEntity;

import rx.Observable;
// TODO: 1/29/16 Change Name!

/**
 * An interface representing a user Cache.
 */
public interface UserCache {
    /**
     * Gets an {@link rx.Observable} which will emit a {@link UserEntity}.
     *
     * @param userId The user id to retrieve data.
     */
    Observable<UserEntity> get(final int userId);

    /**
     * Puts and element into the cache.
     *
     * @param userEntity Element to insert in the cache.
     */
    void put(UserEntity userEntity);

    /**
     * Checks if an element (User) exists in the cache.
     *
     * @param userId The id used to look for inside the cache.
     * @return true if the element is cached, otherwise false.
     */
    boolean isCached(final int userId);

    /**
     * Checks if the cache is expired.
     *
     * @return true, the cache is expired, otherwise false.
     */
    boolean isExpired(/*final int userId*/);

    /**
     * Evict all elements of the cache.
     */
    void evictAll();

//    Observable<Boolean> evictById(final int userId);
//
//    Observable<Boolean> evict(final UserEntity userEntity);
}