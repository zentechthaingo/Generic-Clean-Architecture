package com.zeyad.cleanarchitecture.data.entities.realm_models.incoming;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author by zeyad on 2/06/16.
 */
public class IncomingPivotRealmModel extends RealmObject {
    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    int order_id, store_id;

    public IncomingPivotRealmModel() {
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }
}