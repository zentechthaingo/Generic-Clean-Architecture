package com.zeyad.cleanarchitecture.data.db.generalize;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import java.util.Collection;

import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

public interface GeneralRealmManager {
    /**
     * Gets an {@link Observable} which will emit a {@link UserRealmModel}.
     *
     * @param userId The user id to retrieve data.
     */
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
    void put(RealmObject realmModel);

    /**
     * Puts and element into the cache.
     *
     * @param realmModels Element to insert in the cache.
     */
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

    void evictCollection(Collection<Integer> collection, Class dataClass);

    Context getContext();

    Observable<Collection> getWhere(Class clazz, Func1<RealmQuery, RealmQuery> predicate);
}
