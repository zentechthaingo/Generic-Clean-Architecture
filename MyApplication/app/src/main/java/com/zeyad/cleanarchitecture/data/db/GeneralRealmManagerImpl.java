package com.zeyad.cleanarchitecture.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
            if (finalItemId <= 0)
                finalItemId = Utils.getMaxId(dataClass, idColumnName);
            return Observable.just(Realm.getDefaultInstance()
                    .where(dataClass).equalTo(idColumnName, finalItemId).findFirst());
        });
    }

    @Override
    public Observable<List> getAll(Class clazz) {
        return Observable.defer(() -> Observable.just(Realm.getDefaultInstance().where(clazz).findAll()));
    }

    @Override
    public Observable<List> getWhere(Class clazz, String query, String filterKey) {
        return Observable.defer(() -> Observable.just(Realm.getDefaultInstance()
                .where(clazz).beginsWith(filterKey, query, Case.INSENSITIVE).findAll()));
    }

    @Override
    public Observable<List> getWhere(RealmQuery realmQuery) {
        return Observable.defer(() -> Observable.just(realmQuery.findAll()));
    }

    @Override
    public Observable<?> put(RealmObject realmObject, Class dataClass) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                if (mRealm.isInTransaction())
                    mRealm.cancelTransaction();
                mRealm.beginTransaction();
                RealmObject result = mRealm.copyToRealmOrUpdate(realmObject);
                mRealm.commitTransaction();
                if (RealmObject.isValid(result)) {
//                    mRealm.close();
                    writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                            + dataClass.getSimpleName(), "putRealmObject");
                    return Observable.just(true);
                } else {
//                    mRealm.close();
                    return Observable.error(new Exception("RealmModel is invalid"));
                }
            });
        }
        return Observable.error(new Exception("realmObject cant be null"));
    }

    @Override
    public Observable<?> put(RealmModel realmModel, Class dataClass) {
        if (realmModel != null) {
            return Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                if (mRealm.isInTransaction())
                    mRealm.cancelTransaction();
                mRealm.beginTransaction();
                RealmModel result = mRealm.copyToRealmOrUpdate(realmModel);
                mRealm.commitTransaction();
                if (RealmObject.isValid(result)) {
//                    mRealm.close();
                    writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                            + dataClass.getSimpleName(), "putRealmModel");
                    return Observable.just(true);
                } else {
//                    mRealm.close();
                    return Observable.error(new Exception("RealmModel is invalid"));
                }
            });
        }
        return Observable.error(new Exception("realmModel cant be null"));
    }

    // FIXME: 5/21/16 ugly hack, please fix!
    @Override
    public Observable<?> put(JSONObject realmObject, Class dataClass) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                String fieldName = "";
                List<String> fieldNames = new ArrayList<>();
                for (int i = 0; i < realmObject.names().length(); i++) {
                    fieldName = realmObject.names().optString(i);
                    if (fieldName.toLowerCase().contains("id"))
                        fieldNames.add(fieldName);
                }
                for (String string : fieldNames)
                    if (string.equalsIgnoreCase("id"))
                        fieldName = string;
                if (!fieldName.equalsIgnoreCase("id"))
                    fieldName = fieldNames.get(0);
//                for (int i = 0; i < realmObject.names().length(); i++) {
//                    fieldName = realmObject.names().optString(i);
//                    if (fieldName.toLowerCase().contains("id"))
//                        break;
//                }
                if (!fieldName.isEmpty())
                    try {
                        if (realmObject.getInt(fieldName) == 0)
                            realmObject.put(fieldName, Utils.getNextId(dataClass, fieldName));
                        mRealm = Realm.getDefaultInstance();
                        if (mRealm.isInTransaction())
                            mRealm.cancelTransaction();
                        mRealm.beginTransaction();
                        RealmModel result = mRealm.createOrUpdateObjectFromJson(dataClass, realmObject);
                        mRealm.commitTransaction();
                        if (RealmObject.isValid(result)) {
//                            mRealm.close();
                            writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                                    + dataClass.getSimpleName(), "putJSON");
                            return Observable.just(true);
                        } else {
//                            mRealm.close();
                            return Observable.error(new Exception("RealmModel is invalid"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return Observable.error(e);
                    }
//                    finally {
//                        if (!mRealm.isClosed()) {
//                            if (mRealm.isInTransaction())
//                                mRealm.cancelTransaction();
//                            mRealm.close();
//                        }
//                    }
                return Observable.error(new Exception("could not find id!"));
            });
        }
        return Observable.error(new Exception("json cant be null"));
    }

    @Override
    public void putAll(List<RealmObject> realmModels, Class dataClass) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            if (mRealm.isInTransaction())
                mRealm.cancelTransaction();
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(realmModels);
            mRealm.commitTransaction();
//            mRealm.close();
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + dataClass.getSimpleName(), "putAll");
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
    public Observable<Boolean> evictAll(Class clazz) {
        return Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            RealmResults results = mRealm.where(clazz).findAll();
            boolean areDeleted = true;
            if (!results.isEmpty()) {
                if (mRealm.isInTransaction())
                    mRealm.cancelTransaction();
                mRealm.beginTransaction();
                areDeleted = results.deleteAllFromRealm();
                mRealm.commitTransaction();
            }
//            mRealm.close();
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + clazz.getSimpleName(), "evictAll");
            return Observable.just(areDeleted);
        });
    }


    @Override
    public void evict(final RealmObject realmModel, Class clazz) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            if (mRealm.isInTransaction())
                mRealm.cancelTransaction();
            mRealm.beginTransaction();
            realmModel.deleteFromRealm();
            mRealm.commitTransaction();
            boolean isDeleted = !realmModel.isValid();
//            mRealm.close();
            writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + clazz.getSimpleName(), "evict");
            return Observable.just(isDeleted);
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
                        Log.d(TAG, clazz.getName() + " deleted!");
                    }
                });
    }

    @Override
    public boolean evictById(final long itemId, Class clazz) {
        mRealm = Realm.getDefaultInstance();
        RealmModel toDelete = mRealm.where(clazz).equalTo("userId", itemId).findFirst();
        if (toDelete != null) {
            if (mRealm.isInTransaction())
                mRealm.cancelTransaction();
            mRealm.beginTransaction();
            RealmObject.deleteFromRealm(toDelete);
            mRealm.commitTransaction();
            boolean isDeleted = !RealmObject.isValid(toDelete);
//            mRealm.close();
            writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + clazz.getSimpleName(), "evictById");
            return isDeleted;
        } else return false;
    }

    @Override
    public Observable<?> evictCollection(List<Long> list, Class dataClass) {
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

    @Override
    public boolean isCached(int itemId, String columnId, Class clazz) {
        if (columnId.isEmpty())
            return false;
        Object realmObject = Realm.getDefaultInstance().where(clazz).equalTo(columnId, itemId).findFirst();
        return realmObject != null /*&& ((UserRealmModel) realmObject).getDescription() != null*/;
    }

    @Override
    public boolean isItemValid(int itemId, String columnId, Class clazz) {
        return isCached(itemId, columnId, clazz) && areItemsValid(Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                + clazz.getSimpleName());
    }

    @Override
    public boolean areItemsValid(String destination) {
        return (System.currentTimeMillis() - getFromPreferences(destination)) <= Constants.EXPIRATION_TIME;
    }

    /**
     * Write a value to a user preferences file.
     *
     * @param value  A long representing the value to be inserted.
     * @param source which method is making this call
     */
    private void writeToPreferences(long value, String destination, String source) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(Constants.SETTINGS_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putLong(destination, value);
        editor.apply();
        Log.d(TAG, source + " writeToPreferencesTo " + destination + ": " + value);
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