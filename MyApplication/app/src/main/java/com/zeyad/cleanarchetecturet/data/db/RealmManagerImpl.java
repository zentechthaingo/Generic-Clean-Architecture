package com.zeyad.cleanarchetecturet.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zeyad.cleanarchetecturet.data.entities.UserRealmModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;

/**
 * {@link RealmManager} implementation.
 */
@Singleton
public class RealmManagerImpl implements RealmManager {

    private static final String SETTINGS_FILE_NAME = "com.zeyad.vjs.SETTINGS";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;
    private Realm mRealm;
    private Context context;

    @Inject
    public RealmManagerImpl(Context context) {
        mRealm = Realm.getDefaultInstance();
        this.context = context;
    }

    @Override
    public Observable<UserRealmModel> get(final int userId) {
        mRealm = Realm.getDefaultInstance();
        return mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirstAsync().asObservable();
    }

    @Override
    public Observable<RealmResults<UserRealmModel>> getAll() {
        mRealm = Realm.getDefaultInstance();
        return mRealm.where(UserRealmModel.class).findAllAsync().asObservable();
    }

    @Override
    public void put(final UserRealmModel userEntity) {
        if (userEntity != null) {
            if (!isCached(userEntity.getUserId())) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        mRealm.copyToRealmOrUpdate(userEntity);
                    }
                }, new Realm.Transaction.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("RealmManagerImpl", "UserRealmModel insert complete");
                        writeToPreferences(System.currentTimeMillis());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("RealmManagerImpl", "Failed to insert UserRealmModel", e);
                        // transaction is automatically rolled-back, do any cleanup here
                    }
                });
//                Observable.create(new Observable.OnSubscribe<UserRealmModel>() {
//                    @Override
//                    public void call(final Subscriber<? super UserRealmModel> subscriber) {
//                        mRealm = Realm.getDefaultInstance();
//                        mRealm.beginTransaction();
//                        mRealm.copyToRealmOrUpdate(userEntity);
//                        mRealm.commitTransaction();
//                        writeToPreferences(System.currentTimeMillis());
//                        subscriber.onNext(userEntity);
//                        subscriber.onCompleted();
//                    }
//                }).subscribe(new Observer<UserRealmModel>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(UserRealmModel userRealmModel) {
//
//                    }
//                });
            }
        }
    }

    @Override
    public boolean isCached(int userId) {
        mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        boolean isCached = mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirst() != null;
        mRealm.commitTransaction();
        return isCached;
    }

    @Override
    public boolean isValid(int userId) {
        return isCached(userId) && isValid();
    }

    @Override
    public boolean isValid() {
        if (((System.currentTimeMillis() - getFromPreferences()) > EXPIRATION_TIME)) {
            evictAll();
            return false;
        } else
            return true;
    }

    @Override
    public void evictAll() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                mRealm.where(UserRealmModel.class).findAll().clear();
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
            }

            @Override
            public void onError(Exception e) {
                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
                // transaction is automatically rolled-back, do any cleanup here
            }
        });
//        Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                mRealm = Realm.getDefaultInstance();
//                mRealm.beginTransaction();
//                mRealm.where(UserRealmModel.class).findAll().clear();
//                mRealm.commitTransaction();
//                subscriber.onNext(true);
//                subscriber.onCompleted();
//            }
//        }).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onCompleted() {
//                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
//            }
//
//            @Override
//            public void onNext(Boolean file) {
//                Log.d("RealmManagerImpl", "UserRealmModel evict successful");
//            }
//        });
    }

    @Override
    public void evictById(final int userId) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirst().removeFromRealm();
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
            }

            @Override
            public void onError(Exception e) {
                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
                // transaction is automatically rolled-back, do any cleanup here
            }
        });
//        Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                mRealm = Realm.getDefaultInstance();
//                mRealm.beginTransaction();
//                mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirst().removeFromRealm();
//                mRealm.commitTransaction();
//                subscriber.onNext(true);
//                subscriber.onCompleted();
//            }
//        }).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onCompleted() {
//                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
//            }
//
//            @Override
//            public void onNext(Boolean file) {
//                Log.d("RealmManagerImpl", "UserRealmModel evict successful");
//            }
//        });
    }

    @Override
    public void evict(final UserRealmModel userRealmModel) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                userRealmModel.removeFromRealm();
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
            }

            @Override
            public void onError(Exception e) {
                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
                // transaction is automatically rolled-back, do any cleanup here
            }
        });
//        Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                mRealm = Realm.getDefaultInstance();
//                mRealm.beginTransaction();
//                userRealmModel.removeFromRealm();
//                mRealm.commitTransaction();
//                subscriber.onNext(true);
//                subscriber.onCompleted();
//            }
//        }).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onCompleted() {
//                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
//            }
//
//            @Override
//            public void onNext(Boolean file) {
//                Log.d("RealmManagerImpl", "UserRealmModel evict successful");
//            }
//        });
    }

    /**
     * Write a value to a user preferences file.
     *
     * @param value A long representing the value to be inserted.
     */
    private void writeToPreferences(long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS_FILE_NAME,
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
        return context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(SETTINGS_KEY_LAST_CACHE_UPDATE, 0);
    }

    public Realm getRealm() {
        return mRealm;
    }
}