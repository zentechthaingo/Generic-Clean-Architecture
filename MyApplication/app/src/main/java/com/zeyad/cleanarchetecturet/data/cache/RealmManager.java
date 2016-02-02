package com.zeyad.cleanarchetecturet.data.cache;

import com.zeyad.cleanarchetecturet.data.entity.UserRealmModel;

import io.realm.RealmResults;
import rx.Observable;

/**
 * An interface representing a user Cache.
 */
public interface RealmManager {
    /**
     * Gets an {@link Observable} which will emit a {@link UserRealmModel}.
     *
     * @param userId The user id to retrieve data.
     */
    Observable<UserRealmModel> get(final int userId);

    /**
     * Gets an {@link Observable} which will emit a {@link RealmResults<UserRealmModel>}.
     */
    Observable<RealmResults<UserRealmModel>> getAll();

    /**
     * Puts and element into the cache.
     *
     * @param userRealmModel Element to insert in the cache.
     */
    void put(UserRealmModel userRealmModel);

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
    boolean isValid(final int userId);
    boolean isValid();

    /**
     * Evict all elements of the cache.
     */
    void evictAll();

    void evictById(final int userId);

    void evict(final UserRealmModel userRealmModel);
}