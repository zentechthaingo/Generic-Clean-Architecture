package com.zeyad.cleanarchitecturet.data.db.generalize;

import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

public interface GeneralRealmManager {
    /**
     * Gets an {@link Observable} which will emit a {@link UserRealmModel}.
     *
     * @param userId The user id to retrieve data.
     */
    <T extends RealmObject> Observable<T> get(final int userId, Class clazz);

    /**
     * Gets an {@link Observable} which will emit a {@link RealmResults <UserRealmModel>}.
     */
    <T extends RealmObject> Observable<List<T>> getAll(Class clazz);

    /**
     * Puts and element into the cache.
     *
     * @param realmModel Element to insert in the cache.
     */
    void put(JSONObject realmModel, Class clazz);

    /**
     * Puts and element into the cache.
     *
     * @param realmModels Element to insert in the cache.
     */
    void putAll(JSONArray realmModels, Class clazz);

    /**
     * Checks if an element (User) exists in the cache.
     *
     * @param itemId The id used to look for inside the cache.
     * @return true if the element is cached, otherwise false.
     */
    boolean isCached(final int itemId, Class clazz);

    /**
     * Checks if the cache is expired.
     *
     * @return true, the cache is expired, otherwise false.
     */
    boolean isItemValid(final int itemId, Class clazz);

    boolean areItemsValid(Class clazz);

    /**
     * Evict all elements of the cache.
     */
    void evictAll(Class clazz);

    void evictById(final int itemId, Class clazz);

    void evict(final RealmObject realmModel, Class clazz);
}
