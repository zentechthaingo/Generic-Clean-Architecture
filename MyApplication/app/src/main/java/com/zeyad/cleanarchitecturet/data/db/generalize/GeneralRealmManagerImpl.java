package com.zeyad.cleanarchitecturet.data.db.generalize;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
// TODO: 3/10/16 Test and add queries!

/**
 * {@link GeneralRealmManager} implementation.
 */
@Singleton
public class GeneralRealmManagerImpl implements GeneralRealmManager {

    private static final String TAG = "GeneralRealmManagerImpl", SETTINGS_FILE_NAME = "com.zeyad.cleanarchitecture.SETTINGS",
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
        mRealm = Realm.getDefaultInstance();
        return mRealm.asObservable()
                .map(realm -> realm.where(clazz)
                        .equalTo("itemId", itemId)
                        .findFirstAsync())
                .asObservable();
//        return mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirstAsync().asObservable();
    }

    @Override
    public Observable<Collection> getAll(Class clazz) {
        mRealm = Realm.getDefaultInstance();
        return mRealm.asObservable()
                .map(realm -> realm.where(clazz)
                        .findAllAsync())
                .asObservable()
                .map(userRealmModels -> {
                    ArrayList result = new ArrayList<>();
                    for (int i = 0; i < userRealmModels.size(); i++)
                        result.add(userRealmModels.get(i));
                    return result;
                });
        //userRealmModels -> new ArrayList<T>(userRealmModels.toArray());
//        (Func1<RealmResults<T>, Collection<T>>) Collection<T>::new);
//        return mRealm.where(UserRealmModel.class).findAllAsync().asObservable();
    }

    @Override
    public void put(RealmObject realmObject) {
        if (realmObject != null) {
            Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(final Subscriber<? super Void> subscriber) {
                    mRealm = Realm.getDefaultInstance();
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(realmObject);
                    mRealm.commitTransaction();
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<Void>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Void userRealmModel) {
                            Log.d(TAG, "user added!");
                        }
                    });
        }
    }

    @Override
    public void putAll(Collection<RealmObject> realmModels) {
        Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(realmModels);
                mRealm.commitTransaction();
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Void userRealmModel) {
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
//        mRealm.where(UserRealmModel.class).findAll().clear();
        mRealm.asObservable().map(realm -> {
            mRealm.where(clazz).findAll().clear();
            return null;
        }).subscribe(new Subscriber<Object>() {
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
            return null;
        }).subscribe(new Subscriber<Object>() {
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
        }).subscribe(new Subscriber<Object>() {
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