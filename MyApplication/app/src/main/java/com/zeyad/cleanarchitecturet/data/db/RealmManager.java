package com.zeyad.cleanarchitecturet.data.db;

import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import java.util.List;

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
     * Puts and element into the cache.
     *
     * @param userRealmModels Element to insert in the cache.
     */
    void putAll(List<UserRealmModel> userRealmModels);

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
    boolean isUserValid(final int userId);

    boolean areUsersValid();

    /**
     * Evict all elements of the cache.
     */
    void evictAll();

    void evictById(final int userId);

    void evict(final UserRealmModel userRealmModel);
}