package com.zeyad.cleanarchitecturet.data.db.generalize;

import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;

import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.Observable;

public interface GeneralRealmManager {
    /**
     * Gets an {@link Observable} which will emit a {@link UserRealmModel}.
     *
     * @param userId The user id to retrieve data.
     */
    // TODO: 3/23/16 Generalize!
    Observable<?> get(final int userId, Class clazz);

    /**
     * Gets an {@link Observable} which will emit a {@link RealmResults <UserRealmModel>}.
     */
    Observable<Collection> getAll(Class clazz);

    /**
     * Puts and element into the cache.
     *
     * @param realmModel Element to insert in the cache.
     */
//    void put(JSONObject realmModel, Class clazz);

    void put(RealmObject realmModel);

    /**
     * Puts and element into the cache.
     *
     * @param realmModels Element to insert in the cache.
     */
//    void putAll(JSONArray realmModels, Class clazz);

    void putAll(Collection<RealmObject> realmModels);

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
