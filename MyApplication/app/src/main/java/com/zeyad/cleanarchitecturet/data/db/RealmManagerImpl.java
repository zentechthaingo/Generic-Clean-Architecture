package com.zeyad.cleanarchitecturet.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zeyad.cleanarchitecturet.data.db.leaderboardexample.RealmObservable;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * {@link RealmManager} implementation.
 */
@Singleton
public class RealmManagerImpl implements RealmManager {

    private static final String SETTINGS_FILE_NAME = "com.zeyad.cleanarchitecture.SETTINGS";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;
    private Realm mRealm;
    private Context mContext;

    @Inject
    public RealmManagerImpl(Context mContext) {
        mRealm = Realm.getDefaultInstance();
        this.mContext = mContext;
    }

    @Override
    public Observable<UserRealmModel> get(final int userId) {
        mRealm = Realm.getInstance(mContext);
        return mRealm.asObservable()
                .map(realm -> realm.where(UserRealmModel.class)
                        .equalTo("userId", userId)
                        .findFirstAsync())
                .asObservable();
//        return mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirstAsync().asObservable();
    }

    @Override
    public Observable<RealmResults<UserRealmModel>> getAll() {
        mRealm = Realm.getInstance(mContext);
        return mRealm.asObservable()
                .map(realm -> realm.where(UserRealmModel.class)
                        .findAllAsync())
                .asObservable();
//        return mRealm.where(UserRealmModel.class).findAllAsync().asObservable();
    }

    // FIXME: 3/5/16 access from the same thread!
    @Override
    public void put(final UserRealmModel userRealmModel) {
        if (userRealmModel != null) {
//            if (!isCached(userEntity.getUserId())) {
//            mRealm = Realm.getInstance(mContext);
//            mRealm.beginTransaction();
//            mRealm.copyToRealmOrUpdate(userEntity);
//            mRealm.commitTransaction();
//            mRealm.asObservable()
//                    .map(realm -> mRealm.copyToRealmOrUpdate(userRealmModel))
//                    .observeOn(Schedulers.io())
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(new Subscriber<Object>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onNext(Object o) {
//                            Log.d("RealmManager", "user added!");
//                        }
//                    });
            RealmObservable.object(mContext, realm -> realm.copyToRealm(userRealmModel))
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<UserRealmModel>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(UserRealmModel userRealmModel) {
                            Log.d("RealmManager", "user added!");
                        }
                    });
//            Observable.create(new Observable.OnSubscribe<Void>() {
//                @Override
//                public void call(final Subscriber<? super Void> subscriber) {
//                    mRealm = Realm.getInstance(mContext);
//                    mRealm.beginTransaction();
//                    mRealm.copyToRealm(userRealmModel);
//                    subscriber.onNext(null);
//                    mRealm.commitTransaction();
////                    mRealm.close();
//                    subscriber.onCompleted();
//                }
//            })
////                    .flatMap(Observable::just)
////                    .observeOn(Schedulers.io())
////                    .subscribeOn(Schedulers.io())
//                    .subscribe(new Subscriber<Void>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            e.printStackTrace();
//                        }
//
//                        @Override
//                        public void onNext(Void userRealmModel) {
//                            Log.d("RealmManager", "user added!");
//                        }
//                    });
////            .flatMap(Observable::from);
        }
    }

    @Override
    public void putAll(List<UserRealmModel> userRealmModels) {
        for (UserRealmModel userRealmModel : userRealmModels)
            put(userRealmModel);
    }

    @Override
    public boolean isCached(int userId) {
        mRealm = Realm.getInstance(mContext);
        mRealm.beginTransaction();
        boolean isCached = mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirst() != null;
        mRealm.commitTransaction();
        return isCached;
    }

    @Override
    public boolean isUserValid(int userId) {
        return isCached(userId) && areUsersValid();
    }

    @Override
    public boolean areUsersValid() {
        if (((System.currentTimeMillis() - getFromPreferences()) > EXPIRATION_TIME)) {
//            evictAll();
            return false;
        } else
            return true;
    }

    // FIXME: 3/5/16 access from the same thread!
    @Override
    public void evictAll() {
        mRealm = Realm.getInstance(mContext);
//        mRealm.where(UserRealmModel.class).findAll().clear();
        mRealm.asObservable().map(realm -> {
            mRealm.where(UserRealmModel.class).findAll().clear();
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
                Log.d("RealmManager", "all users deleted!");
            }
        });
    }

    @Override
    public void evictById(final int userId) {
        mRealm = Realm.getInstance(mContext);
        mRealm.asObservable().map(realm -> {
            mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirst().removeFromRealm();
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
                Log.d("RealmManager", "all users deleted!");
            }
        });
    }

    @Override
    public void evict(final UserRealmModel userRealmModel) {
        mRealm = Realm.getInstance(mContext);
        mRealm.asObservable().map(aVoid -> {
            userRealmModel.removeFromRealm();
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
                Log.d("RealmManager", "all users deleted!");
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

    public Realm getRealm() {
        return mRealm;
    }
}