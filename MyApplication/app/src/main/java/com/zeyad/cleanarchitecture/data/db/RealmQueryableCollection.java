package com.zeyad.cleanarchitecture.data.db;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmQuery;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class RealmQueryableCollection {

    private List<RealmQueryable> queryables;

    public RealmQueryableCollection() {
        queryables = new ArrayList<>();
    }

    public List<RealmQueryable> getQuerables(Class clazz) {
        final List<RealmQueryable> filtered = new ArrayList<>();
        Observable.from(queryables)
                .filter(q -> q.getClazz().equals(clazz))
                .subscribe(q -> filtered.add(q));
        return filtered;
    }

    public void add(Class clazz, Func1<RealmQuery, RealmQuery> predicate, BehaviorSubject subject) {
        RealmQueryable realmQuerable = new RealmQueryable(clazz, predicate, subject);
        queryables.add(realmQuerable);
    }

    public void clear() {
        queryables.clear();
    }


    public List<RealmQueryable> getQueryables() {
        return queryables;
    }
}