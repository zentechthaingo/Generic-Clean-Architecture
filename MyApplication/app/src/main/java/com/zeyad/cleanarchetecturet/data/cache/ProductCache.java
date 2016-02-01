package com.zeyad.cleanarchetecturet.data.cache;

import com.zeyad.cleanarchetecturet.data.entity.ProductEntity;

import rx.Observable;
// TODO: 1/29/16 Change Name!

/**
 * An interface representing a user Cache.
 */
public interface ProductCache {
    /**
     * Gets an {@link Observable} which will emit a {@link ProductEntity}.
     *
     * @param userId The user id to retrieve data.
     */
    Observable<ProductEntity> get(final int userId);

    /**
     * Puts and element into the cache.
     *
     * @param productEntity Element to insert in the cache.
     */
    void put(ProductEntity productEntity);

    /**
     * Checks if an element (User) exists in the cache.
     *
     * @param productId The id used to look for inside the cache.
     * @return true if the element is cached, otherwise false.
     */
    boolean isCached(final int productId);

    /**
     * Checks if the cache is expired.
     *
     * @return true, the cache is expired, otherwise false.
     */
    boolean isExpired(/*final int productId*/);

    /**
     * Evict all elements of the cache.
     */
    void evictAll();

    Observable<Boolean> evictById(final int userId);

    Observable<Boolean> evict(final ProductEntity productEntity);
}