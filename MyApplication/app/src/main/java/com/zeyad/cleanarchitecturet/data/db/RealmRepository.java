package com.zeyad.cleanarchitecturet.data.db;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.RealmQuery;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public interface RealmRepository {
    <T> Observable<T> get(Class clazz, Func1<RealmQuery, RealmQuery> predicate);

    void storeObject(Class clazz, JSONObject jsonObject);

    void storeObjects(Class clazz, JSONArray jsonArray);

    <T> Observable<T> update(Class clazz, Action0 action);
}