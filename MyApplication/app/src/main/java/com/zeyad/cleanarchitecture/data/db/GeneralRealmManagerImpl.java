package com.zeyad.cleanarchitecture.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.grability.rappitendero.utils.Constants;
import com.grability.rappitendero.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * {@link DataBaseManager} implementation.
 */
@Singleton
public class GeneralRealmManagerImpl implements DataBaseManager {

    public final String TAG = GeneralRealmManagerImpl.class.getName();
    private Realm mRealm;
    private Context mContext;

    @Inject
    public GeneralRealmManagerImpl(Context mContext) {
        mRealm = Realm.getDefaultInstance();
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Observable<?> getById(@NonNull final String idColumnName, final int itemId, Class dataClass) {
        return Observable.defer(() -> {
            int finalItemId = itemId;
            if (finalItemId <= 0)
                finalItemId = Utils.getMaxId(dataClass, idColumnName);
            return Observable.just(Realm.getDefaultInstance()
                    .where(dataClass).equalTo(idColumnName, finalItemId).findFirst());
        });
    }

    @NonNull
    @Override
    public Observable<List> getAll(Class clazz) {
        return Observable.defer(() -> Observable.just(Realm.getDefaultInstance().where(clazz).findAll()));
    }

    @NonNull
    @Override
    public Observable<List> getWhere(Class clazz, String query, @NonNull String filterKey) {
        return Observable.defer(() -> Observable.just(Realm.getDefaultInstance()
                .where(clazz).beginsWith(filterKey, query, Case.INSENSITIVE).findAll()));
    }

    @NonNull
    @Override
    public Observable<List> getWhere(@NonNull RealmQuery realmQuery) {
        return Observable.defer(() -> Observable.just(realmQuery.findAll()));
    }

    @NonNull
    @Override
    public Observable<?> put(@Nullable RealmObject realmObject, @NonNull Class dataClass) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                RealmObject result = executeWriteOperationInRealm(mRealm, () -> mRealm.copyToRealmOrUpdate(realmObject));
                if (RealmObject.isValid(result)) {
                    writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                            + dataClass.getSimpleName(), "putRealmObject");
                    return Observable.just(true);
                } else
                    return Observable.error(new Exception("RealmObject is invalid"));
            });
        }
        return Observable.error(new Exception("realmObject cant be null"));
    }

    @NonNull
    @Override
    public Observable<?> put(@Nullable RealmModel realmModel, @NonNull Class dataClass) {
        if (realmModel != null) {
            return Observable.defer(() -> {
                mRealm = Realm.getDefaultInstance();
                RealmModel result = executeWriteOperationInRealm(mRealm, () -> mRealm.copyToRealmOrUpdate(realmModel));
                if (RealmObject.isValid(result)) {
                    writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                            + dataClass.getSimpleName(), "putRealmModel");
                    return Observable.just(true);
                } else
                    return Observable.error(new Exception("RealmModel is invalid"));
            });
        }
        return Observable.error(new Exception("realmModel cant be null"));
    }

    @NonNull
    @Override
    public Observable<?> put(@Nullable JSONObject realmObject, @Nullable String idColumnName, @NonNull Class dataClass) {
        if (realmObject != null) {
            return Observable.defer(() -> {
                if (idColumnName == null || idColumnName.isEmpty())
                    return Observable.error(new Exception("could not find id!"));
                try {
                    if (realmObject.getInt(idColumnName) == 0)
                        realmObject.put(idColumnName, Utils.getNextId(dataClass, idColumnName));
                    mRealm = Realm.getDefaultInstance();
                    RealmModel result = executeWriteOperationInRealm(mRealm, () -> mRealm.createOrUpdateObjectFromJson(dataClass, realmObject));
                    if (RealmObject.isValid(result)) {
                        writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                                + dataClass.getSimpleName(), "putJSON");
                        return Observable.just(true);
                    } else
                        return Observable.error(new Exception("RealmModel is invalid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });
        } else
            return Observable.error(new Exception("json cant be null"));
    }

    @NonNull
    @Override
    public Observable putAll(JSONArray jsonArray, @NonNull Class dataClass) {
        return Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            executeWriteOperationInRealm(mRealm, () -> mRealm.createOrUpdateAllFromJson(dataClass, jsonArray));
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + dataClass.getSimpleName(), "putAll");
            return Observable.just(true);
        });
    }

    @Override
    public void putAll(@NonNull List<RealmObject> realmModels, @NonNull Class dataClass) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            executeWriteOperationInRealm(mRealm, () -> mRealm.copyToRealmOrUpdate(realmModels));
            writeToPreferences(System.currentTimeMillis(), Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + dataClass.getSimpleName(), "putAll");
            return Observable.from(realmModels);
        }).subscribeOn(Schedulers.immediate())
                //we need to use immediate instead of io,
                // since if we have created UI thread realm objects,
                // and we try to invoke this method.
                // Then this method would not use if IO is used,
                // since io creates a new thread for each subscriber.
                .subscribe(new PutAllSubscriberClass(realmModels));
    }

    @NonNull
    @Override
    public Observable<Boolean> evictAll(@NonNull Class clazz) {
        return Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            executeWriteOperationInRealm(mRealm, () -> mRealm.delete(clazz));
            writeToPreferences(System.currentTimeMillis()
                    , Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE
                            + clazz.getSimpleName(), "evictAll");
            return Observable.just(true);
        });
    }

    @Override
    public void evict(@NonNull final RealmObject realmModel, @NonNull Class clazz) {
        Observable.defer(() -> {
            mRealm = Realm.getDefaultInstance();
            executeWriteOperationInRealm(mRealm, (Executor) realmModel::deleteFromRealm);
            boolean isDeleted = !realmModel.isValid();
            writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + clazz.getSimpleName(), "evict");
            return Observable.just(isDeleted);
        }).subscribeOn(Schedulers.immediate())
                //we need to use immediate instead of io,
                // since if we have created UI thread realm objects,
                // and we try to invoke this method.
                // Then this method would not use if IO is used,
                // since io creates a new thread for each subscriber.
                .subscribe(new EvictSubscriberClass(clazz));
    }

    @Override
    public boolean evictById(@NonNull Class clazz, @NonNull String idFieldName, final long idFieldValue) {
        mRealm = Realm.getDefaultInstance();
        RealmModel toDelete = mRealm.where(clazz).equalTo(idFieldName, idFieldValue).findFirst();
        if (toDelete != null) {
            executeWriteOperationInRealm(mRealm, () -> RealmObject.deleteFromRealm(toDelete));
            boolean isDeleted = !RealmObject.isValid(toDelete);
            writeToPreferences(System.currentTimeMillis(), Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                    + clazz.getSimpleName(), "evictById");
            return isDeleted;
        } else return false;
    }

    @NonNull
    @Override
    public Observable<?> evictCollection(@NonNull String idFieldName, @NonNull List<Long> list, @NonNull Class
            dataClass) {
        return Observable.defer(() -> {
            boolean isDeleted = true;
            for (int i = 0; i < list.size(); i++)
                isDeleted = isDeleted && evictById(dataClass, idFieldName, list.get(i));
            return Observable.just(isDeleted);
        });
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public boolean isCached(int itemId, @NonNull String columnId, Class clazz) {
        if (columnId.isEmpty())
            return false;
        Object realmObject = Realm.getDefaultInstance().where(clazz).equalTo(columnId, itemId).findFirst();
        return realmObject != null /*&& ((UserRealmModel) realmObject).getDescription() != null*/;
    }

    @Override
    public boolean isItemValid(int itemId, @NonNull String columnId, @NonNull Class clazz) {
        return isCached(itemId, columnId, clazz) && areItemsValid(Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE
                + clazz.getSimpleName());
    }

    @Override
    public boolean areItemsValid(String destination) {
        return (System.currentTimeMillis() - getFromPreferences(destination)) <= Constants.EXPIRATION_TIME;
    }

    private void executeWriteOperationInRealm(@NonNull Realm realm, @NonNull Executor executor) {
        if (realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        executor.run();
        realm.commitTransaction();
    }

    private <T> T executeWriteOperationInRealm(@NonNull Realm realm, @NonNull ExecuteAndReturn<T> executor) {
        T toReturnValue;
        if (realm.isInTransaction())
            realm.cancelTransaction();
        realm.beginTransaction();
        toReturnValue = executor.run();
        realm.commitTransaction();
        return toReturnValue;
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

    private interface Executor {
        void run();
    }

    private interface ExecuteAndReturn<T> {
        @NonNull
        T run();
    }

    private class EvictSubscriberClass extends Subscriber<Object> {
        private final Class mClazz;

        public EvictSubscriberClass(Class clazz) {
            mClazz = clazz;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Object o) {
            Log.d(TAG, mClazz.getName() + " deleted!");
        }
    }

    private class PutAllSubscriberClass extends Subscriber<Object> {
        private final List<RealmObject> mRealmModels;

        public PutAllSubscriberClass(List<RealmObject> realmModels) {
            mRealmModels = realmModels;
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onNext(Object o) {
            Log.d(TAG, "all " + mRealmModels.getClass().getName() + "s added!");
        }
    }
}