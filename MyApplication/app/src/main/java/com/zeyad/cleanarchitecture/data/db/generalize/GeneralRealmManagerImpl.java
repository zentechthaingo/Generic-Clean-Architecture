package com.zeyad.cleanarchitecture.data.db.generalize;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * {@link GeneralRealmManager} implementation.
 */
@Singleton
public class GeneralRealmManagerImpl implements GeneralRealmManager {

    private static final String TAG = "GeneralRealmManagerImpl",
            SETTINGS_FILE_NAME = "com.zeyad.cleanarchitecture.SETTINGS",
            SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;
    private Realm mRealm;
    private Context mContext;

    @Inject
    public GeneralRealmManagerImpl(Context mContext) {
        mRealm = Realm.getDefaultInstance();
        this.mContext = mContext;
    }

    @Override
    public Observable<?> get(final int itemId, Class clazz) {
        return Realm.getDefaultInstance().asObservable()
                .map(realm -> realm.where(clazz)
                        .equalTo("itemId", itemId)
                        .findFirstAsync())
                .asObservable();
    }

    @Override
    public Observable<Collection> getAll(Class clazz) {
        return Realm.getDefaultInstance().asObservable()
                .map(realm -> realm.where(clazz)
                        .findAllAsync())
                .asObservable()
                .map(userRealmModels -> Arrays.asList(userRealmModels.toArray()));
    }

    @Override
    public Observable<Collection> getWhere(Class clazz, Func1<RealmQuery, RealmQuery> predicate) {
        return Realm.getDefaultInstance()
                .asObservable()
                .map(realm -> {
                    RealmQuery query = realm.where(clazz);
                    if (predicate != null)
                        query = predicate.call(query);
                    return Arrays.asList(query.findAllAsync().toArray());
                })
                .asObservable()
                .map(userRealmModels -> Arrays.asList(userRealmModels.toArray()));
    }

    @Override
    public void put(RealmObject realmObject) {
        if (realmObject != null) {
            Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(realmObject);
                mRealm.commitTransaction();
                return Observable.just(realmObject);
            }).subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<RealmObject>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(RealmObject userRealmModel) {
                            Log.d(TAG, "user added!");
                        }
                    });
        }
    }

    @Override
    public void putAll(Collection<RealmObject> realmModels) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(realmModels);
            mRealm.commitTransaction();
            return Observable.just(realmModels);
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Collection<RealmObject>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Collection<RealmObject> userRealmModel) {
                        Log.d(TAG, "users added or updated!");
                    }
                });
    }

    @Override
    public boolean isCached(int itemId, Class clazz) {
        mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        boolean isCached = mRealm.where(clazz).equalTo("userId", itemId).findFirst() != null;
        mRealm.commitTransaction();
        return isCached;
    }

    @Override
    public boolean isItemValid(int itemId, Class clazz) {
        return isCached(itemId, clazz) && areItemsValid(clazz);
    }

    @Override
    public boolean areItemsValid(Class clazz) {
        return ((System.currentTimeMillis() - getFromPreferences()) <= EXPIRATION_TIME);
    }

    // FIXME: 3/5/16 access from the same thread!
    @Override
    public void evictAll(Class clazz) {
        mRealm = Realm.getDefaultInstance();
        mRealm.asObservable().map(realm -> {
            mRealm.where(clazz).findAll().clear();
            return null;
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, "all " + clazz.getSimpleName() + "s deleted!");
                    }
                });
    }

    @Override
    public void evictById(final int itemId, Class clazz) {
        mRealm = Realm.getDefaultInstance();
        mRealm.asObservable().map(realm -> {
            mRealm.where(clazz).equalTo("userId", itemId).findFirst().removeFromRealm();
            return true;
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, clazz.getSimpleName() + " deleted!");
                    }
                });
    }

    @Override
    public void evict(final RealmObject realmModel, Class clazz) {
        mRealm = Realm.getDefaultInstance();
        mRealm.asObservable().map(aVoid -> {
            realmModel.removeFromRealm();
            return null;
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, clazz.getSimpleName() + " deleted!");
                    }
                });
    }

    @Override
    public void evictCollection(Collection<Integer> collection, Class dataClass) {
        mRealm = Realm.getDefaultInstance();
        mRealm.asObservable().map(aVoid -> {
            for (Integer id : collection)
                evictById(id, dataClass);
            return true;
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, dataClass.getSimpleName() + " deleted!");
                    }
                });
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * Write a value to a user preferences file.
     *
     * @param value A long representing the value to be inserted.
     */
    private void writeToPreferences(long value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SETTINGS_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putLong(SETTINGS_KEY_LAST_CACHE_UPDATE, value);
        editor.apply();
    }

    /**
     * Get a value from a user preferences file.
     *
     * @return A long representing the value retrieved from the preferences file.
     */
    private long getFromPreferences() {
        return mContext.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(SETTINGS_KEY_LAST_CACHE_UPDATE, 0);
    }

    public Realm getRealm() {
        return mRealm;
    }

//    // TODO: 3/14/16 Check it out!
//    RealmQueryableCollection realmQueryCollection;
//
//    public <T> Observable<T> get(Class clazz, Func1<RealmQuery, RealmQuery> predicate) {
//        BehaviorSubject<T> behaviorSubject = BehaviorSubject.create((T) getInner(clazz, predicate));
//        realmQueryCollection.add(clazz, predicate, behaviorSubject);
//        return behaviorSubject;
//    }
//
//    public <T extends RealmObject> RealmResults<T> getInner(Class clazz, Func1<RealmQuery, RealmQuery> predicate) {
//        RealmQuery query = mRealm.where(clazz);
//        if (predicate != null)
//            query = predicate.call(query);
//        return query.findAllAsync();
//    }
//
//    private void notifyObservers(Class clazz) {
//        Observable.from(realmQueryCollection.getQuerables(clazz))
//                .subscribe(realmQuerable -> {
//                    if (!realmQuerable.getSubject().hasObservers()) {
//                        realmQueryCollection.getQueryables().remove(realmQuerable);
//                    } else {
//                        RealmResults realmResults = getInner(clazz, realmQuerable.getPredicate());
//                        realmResults.load();
//                        realmQuerable.getSubject().onNext(realmResults);
//                    }
//                });
//    }
}