package com.zeyad.cleanarchitecture.data.db.generalize;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * {@link GeneralRealmManager} implementation.
 */
@Singleton
public class GeneralRealmManagerImpl implements GeneralRealmManager {

    public static final String TAG = "GeneralRealmManagerImpl",
            SETTINGS_FILE_NAME = "com.zeyad.cleanarchitecture.SETTINGS",
            COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE = "collection_last_cache_update",
            DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE = "detail_last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;
    private Realm mRealm;
    private Context mContext;

    @Inject
    public GeneralRealmManagerImpl(Context mContext) {
        mRealm = Realm.getDefaultInstance();
        this.mContext = mContext;
    }

    @Override
    public Observable<?> getById(final int itemId, Class clazz) {
        return Observable.defer(() ->
                Observable.just(Realm.getDefaultInstance()
                        .where(clazz).equalTo("userId", itemId).findFirst()));
    }

    @Override
    public Observable<Collection> getAll(Class clazz) {
        return Observable.defer(() -> Observable.from(Arrays.asList(Realm.getDefaultInstance().allObjects(clazz))));
    }

    @Override
    public Observable<Collection> getWhere(Class clazz, Func1<RealmQuery, RealmQuery> predicate) {
//        BehaviorSubject behaviorSubject = BehaviorSubject.create(getInner(clazz, predicate));
//        realmQueryCollection.add(clazz, predicate, behaviorSubject);
//        return Observable.from(realmQueryCollection.getQuerables(clazz))
//                .subscribe(realmQuerable -> {
//                    if (!realmQuerable.getSubject().hasObservers()) {
//                        realmQueryCollection.getQueryables().remove(realmQuerable);
//                    } else {
//                        RealmResults realmResults = getInner(clazz, realmQuerable.getPredicate());
//                        realmResults.load();
//                        realmQuerable.getSubject().onNext(realmResults);
//                    }
//                });
        return Observable.defer(() -> {
            RealmQuery query = Realm.getDefaultInstance().where(clazz);
            if (predicate != null)
                query = predicate.call(query);
            return Observable.from(Arrays.asList(query.findAll()));
        });
    }

    @Override
    public Observable<?> put(RealmObject realmObject) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(realmObject);
                mRealm.commitTransaction();
                writeToPreferences(System.currentTimeMillis(), DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE);
                return Observable.just(realmObject);
            });
        }
        return Observable.empty();
    }

    @Override
    public void putAll(Collection<RealmObject> realmModels) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(realmModels);
            mRealm.commitTransaction();
            writeToPreferences(System.currentTimeMillis(), COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE);
            return Observable.from(realmModels);
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
                        Log.d(TAG, "all " + realmModels.getClass().getName() + "s added!");
                    }
                });
    }

    @Override
    public boolean isCached(int itemId, Class clazz) {
        mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        UserRealmModel realmObject = mRealm.where(UserRealmModel.class).equalTo("userId", itemId).findFirst();
        boolean isCached = realmObject != null;
        isCached = isCached && realmObject.getCoverUrl() != null;
        mRealm.commitTransaction();
        mRealm.close();
        return isCached;
    }

    @Override
    public boolean isItemValid(int itemId, Class clazz) {
        return isCached(itemId, clazz) && areItemsValid(DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE);
    }

    @Override
    public boolean areItemsValid(String destination) {
        return (System.currentTimeMillis() - getFromPreferences(destination)) <= EXPIRATION_TIME;
    }

    @Override
    public void evictAll(Class clazz) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            RealmResults results = mRealm.where(clazz).findAll();
            mRealm.beginTransaction();
            results.clear();
            mRealm.commitTransaction();
            return Observable.just(results.isValid());
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
    public void evict(final RealmObject realmModel, Class clazz) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            mRealm.beginTransaction();
            realmModel.removeFromRealm();
            mRealm.commitTransaction();
            return Observable.just(realmModel.isValid());
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
    public boolean evictById(final int itemId, Class clazz) {
        mRealm = Realm.getDefaultInstance();
        RealmObject toDelete = mRealm.where(clazz).equalTo("userId", itemId).findFirst();
        if (toDelete != null) {
            mRealm.beginTransaction();
            toDelete.removeFromRealm();
            mRealm.commitTransaction();
            mRealm.close();
            return toDelete.isValid();
        } else return true;

//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<Object>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        Log.d(TAG, clazz.getSimpleName() + " deleted!");
//                    }
//                });
    }

    @Override
    public Observable<?> evictCollection(Collection<Integer> collection, Class dataClass) {
        return Observable.defer(() -> {
            boolean isDeleted = true;
            for (int i = 0; i < collection.size(); i++)
                isDeleted = isDeleted && !evictById(collection.iterator().next(), dataClass);
            return Observable.just(isDeleted);
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
    private void writeToPreferences(long value, String destination) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SETTINGS_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putLong(destination, value);
        editor.apply();
        Log.d(TAG, "writeToPreferencesTo " + destination + ": " + value);
    }

    /**
     * Get a value from a user preferences file.
     *
     * @return A long representing the value retrieved from the preferences file.
     */
    private long getFromPreferences(String destination) {
        return mContext.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(destination, 0);
    }

    public Realm getRealm() {
        return mRealm;
    }

    //    // TODO: 3/14/16 Check it out!
//    RealmQueryableCollection realmQueryCollection;

    //
//    public <T> Observable<T> getById(Class clazz, Func1<RealmQuery, RealmQuery> predicate) {
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