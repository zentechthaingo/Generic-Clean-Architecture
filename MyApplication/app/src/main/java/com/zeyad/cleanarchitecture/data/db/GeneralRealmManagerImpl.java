package com.zeyad.cleanarchitecture.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * {@link GeneralRealmManager} implementation.
 */
@Singleton
public class GeneralRealmManagerImpl implements GeneralRealmManager {

    public final String TAG = GeneralRealmManagerImpl.class.getName();
    ;
    private Realm mRealm;
    private Context mContext;

    @Inject
    public GeneralRealmManagerImpl(Context mContext) {
        mRealm = Realm.getDefaultInstance();
        this.mContext = mContext;
    }

    @Override
    public Observable<?> getById(final String idColumnName, final int itemId, Class dataClass) {
        return Observable.defer(() -> {
            int finalItemId = itemId;
            if (itemId <= 0)
                finalItemId = Realm.getDefaultInstance().where(dataClass).max(idColumnName).intValue();
            return Observable.just(Realm.getDefaultInstance()
                    .where(dataClass).equalTo(idColumnName, finalItemId).findFirst());
        });
    }

    @Override
    public Observable<List> getAll(Class clazz) {
        return Observable.defer(() -> Observable.from(Collections
                .singletonList(Realm.getDefaultInstance()
                        .where(clazz)
                        .findAll())));
    }

    @Override
    public Observable<List> getWhere(Class clazz, String query, String filterKey) {
        return Observable.defer(() -> Observable.from(Collections
                .singletonList(Realm.getDefaultInstance()
                        .where(clazz)
                        .beginsWith(filterKey, query, Case.INSENSITIVE)
                        .findAll())));
    }

    // TODO: 5/4/16 Implement!
    @Override
    public Observable<List> getWhere(Class clazz, RealmQuery realmQuery) {
        return null;
    }

    @Override
    public Observable<?> put(RealmObject realmObject) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                Observable observable = Observable.just(mRealm.copyToRealmOrUpdate(realmObject));
                mRealm.commitTransaction();
                writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE);
                mRealm.close();
                return observable;
            });
        }
        return Observable.error(new Exception("realmObject cant be null"));
    }

    @Override
    public Observable<?> put(RealmModel realmModel) {
        if (realmModel != null) {
            return Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                Observable observable = Observable.just(mRealm.copyToRealmOrUpdate(realmModel));
                mRealm.commitTransaction();
                writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE);
                mRealm.close();
                return observable;
            });
        }
        return Observable.error(new Exception("realmModel cant be null"));
    }

    @Override
    public Observable<?> put(JSONObject realmObject, Class dataClass) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                if (realmObject.optInt("id") == 0)
                    try {
                        realmObject.put("id", Utils.getNextId(dataClass, "id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                Observable observable = Observable.just(mRealm.createOrUpdateObjectFromJson(dataClass, realmObject));
                mRealm.commitTransaction();
                writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE);
                mRealm.close();
                return observable;
            });
        }
        return Observable.error(new Exception("json cant be null"));
    }

    @Override
    public void putAll(List<RealmObject> realmModels) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(realmModels);
            mRealm.commitTransaction();
//            mRealm.close();
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE);
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
        Object realmObject = mRealm.where(clazz).equalTo("id", itemId).findFirst();
        return realmObject != null;
    }

    @Override
    public boolean isItemValid(int itemId, Class clazz) {
        return /*isCached(itemId, clazz) &&*/ areItemsValid(Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE);
    }

    @Override
    public boolean areItemsValid(String destination) {
        return (System.currentTimeMillis() - getFromPreferences(destination)) <= Constants.EXPIRATION_TIME;
    }

    @Override
    public void evictAll(Class clazz) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            RealmResults results = mRealm.where(clazz).findAll();
            mRealm.beginTransaction();
            results.deleteAllFromRealm();
            mRealm.commitTransaction();
            mRealm.close();
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE);
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
            realmModel.deleteFromRealm();
            mRealm.commitTransaction();
            mRealm.close();
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE);
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
        RealmModel toDelete = mRealm.where(clazz).equalTo("userId", itemId).findFirst();
        if (toDelete != null) {
            mRealm.beginTransaction();
            RealmObject.deleteFromRealm(toDelete);
            mRealm.commitTransaction();
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE);
            return !RealmObject.isValid(toDelete);
        } else return false;
    }

    @Override
    public Observable<?> evictCollection(List<Integer> list, Class dataClass) {
        return Observable.defer(() -> {
            boolean isDeleted = true;
            for (int i = 0; i < list.size(); i++)
                isDeleted = isDeleted && evictById(list.get(i), dataClass);
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
        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.SETTINGS_FILE_NAME,
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
        return mContext.getSharedPreferences(Constants.SETTINGS_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(destination, 0);
    }

    public Realm getRealm() {
        return mRealm;
    }
}