package com.zeyad.cleanarchitecture.data.repository.generalstore;

import android.content.Context;
import android.util.Log;

import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.exceptions.NetworkConnectionException;
import com.zeyad.cleanarchitecture.data.network.RestApi;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CloudDataStore implements DataStore {

    private static final String TAG = CloudDataStore.class.getName();
    private Context mContext;
    private RestApi mRestApi;
    private GeneralRealmManager mRealmManager;
    private EntityMapper mEntityDataMapper;
    private Class mDataClass;
    private final Action1<Object> saveGenericToCacheAction = object -> {
        Object mappedObject = mEntityDataMapper.transformToRealm(object, mDataClass);
        Observable<?> observable;
        if (mappedObject instanceof RealmObject)
            observable = mRealmManager.put((RealmObject) mappedObject, mDataClass);
        else if (mappedObject instanceof RealmModel)
            observable = mRealmManager.put((RealmModel) mappedObject, mDataClass);
        else
            try {
                observable = mRealmManager.put(new JSONObject(new Gson().toJson(object, mDataClass)),
                        mDataClass);
            } catch (JSONException e) {
                e.printStackTrace();
                observable = Observable.error(e);
            }
        observable.subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, object.getClass().getName() + " completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d(TAG, object.getClass().getName() + " added!");
                    }
                });
    };
    private final Action1<List> saveAllGenericsToCacheAction = collection -> {
        List<RealmObject> realmObjectCollection = new ArrayList<>();
        realmObjectCollection.addAll(mEntityDataMapper.transformAllToRealm(collection, mDataClass));
        mRealmManager.putAll(realmObjectCollection, mDataClass);
    };
    private final Action1<List> deleteCollectionGenericsFromCacheAction = collection -> {
        List<RealmObject> realmObjectList = new ArrayList<>();
        realmObjectList.addAll(mEntityDataMapper.transformAllToRealm(collection, mDataClass));
        for (RealmObject realmObject : realmObjectList)
            mRealmManager.evict(realmObject, mDataClass);
    };
    private final Action1<List> deleteAllGenericsFromCacheAction = collection -> mRealmManager.evictAll(mDataClass)
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<Boolean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Boolean object) {
                    Log.d(TAG, collection.getClass().getName() + "s deleted!");
                }
            });

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param restApi      The {@link RestApi} implementation to use.
     * @param realmManager A {@link GeneralRealmManager} to cache data retrieved from the api.
     */
    public CloudDataStore(RestApi restApi, GeneralRealmManager realmManager, EntityMapper entityDataMapper) {
        mRestApi = restApi;
        mEntityDataMapper = entityDataMapper;
        mRealmManager = realmManager;
        mContext = mRealmManager.getContext().getApplicationContext();
//        mGson = new Gson();
    }

    @Override
    public Observable<List> dynamicList(String url, Class domainClass, Class dataClass, boolean persist) {
        mDataClass = dataClass;
        return mRestApi.dynamicGetList(url)
//                .compose(applyExponentialBackoff())
//                .toBlocking()
                .doOnNext(list -> {
                    if (persist)
                        saveAllGenericsToCacheAction.call(list);
                })
                .map(entities -> mEntityDataMapper.transformAllToDomain(entities, domainClass));
    }

    @Override
    public Observable<List> dynamicList(String url, Class domainClass, Class dataClass, boolean persist,
                                        boolean shouldCache) {
        mDataClass = dataClass;
        return mRestApi.dynamicGetList(url, shouldCache)
//                .compose(applyExponentialBackoff())
//                .toBlocking()
                .doOnNext(list -> {
                    if (persist)
                        saveAllGenericsToCacheAction.call(list);
                })
                .map(entities -> mEntityDataMapper.transformAllToDomain(entities, domainClass));
    }

    @Override
    public Observable<?> dynamicObject(String url, String idColumnName, int itemId, Class domainClass,
                                       Class dataClass, boolean persist) {
        mDataClass = dataClass;
        return mRestApi.dynamicGetObject(url)
                .compose(applyExponentialBackoff())
//                .toBlocking()
                .doOnNext(object -> {
                    if (persist)
                        saveGenericToCacheAction.call(object);
                })
                .map(entity -> mEntityDataMapper.transformToDomain(entity, domainClass));
    }

    @Override
    public Observable<?> dynamicObject(String url, String idColumnName, int itemId, Class domainClass,
                                       Class dataClass, boolean persist, boolean shouldCache) {
        mDataClass = dataClass;
        return mRestApi.dynamicGetObject(url, shouldCache)
                .compose(applyExponentialBackoff())
//                .toBlocking()
                .doOnNext(object -> {
                    if (persist)
                        saveGenericToCacheAction.call(object);
                })
                .map(entity -> mEntityDataMapper.transformToDomain(entity, domainClass));
    }

    @Override
    public Observable<?> dynamicPostObject(String url, HashMap<String, Object> keyValuePairs,
                                           Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queuePost.call(object);
                if (persist)
                    saveGenericToCacheAction.call(keyValuePairs);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
            return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    new JSONObject(keyValuePairs).toString()))
                    .compose(applyExponentialBackoff())
//                .toBlocking()
                    .doOnNext(object -> {
                        if (persist)
                            saveGenericToCacheAction.call(object);
                    })
                    .doOnError(throwable -> {
//                        queuePost.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<?> dynamicPostObject(String url, JSONObject keyValuePairs, Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queuePost.call(object);
                if (persist)
                    saveGenericToCacheAction.call(keyValuePairs);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
            return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    keyValuePairs.toString()))
                    .compose(applyExponentialBackoff())
//                .toBlocking()
                    .doOnNext(object -> {
                        if (persist)
                            saveGenericToCacheAction.call(object);
                    })
                    .doOnError(throwable -> {
//                        queuePost.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<List> dynamicPostList(final String url, final HashMap<String, Object> keyValuePairs,
                                            Class domainClass, Class dataClass, boolean persist) {
        mDataClass = dataClass;
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queuePost.call(object);
                if (persist)
                    saveGenericToCacheAction.call(keyValuePairs);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
            return mRestApi.dynamicPostList(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    new JSONObject(keyValuePairs).toString()))
                    .compose(applyExponentialBackoff())
//                .toBlocking()
                    .doOnNext(saveGenericToCacheAction)
                    .doOnError(throwable -> {
                        if (persist)
                            saveGenericToCacheAction.call(keyValuePairs);
//                        queuePost.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<?> dynamicDeleteCollection(final String url, final HashMap<String, Object> keyValuePairs,
                                                 Class dataClass, boolean persist) {
        mDataClass = dataClass;
        List<Integer> ids = (List<Integer>) keyValuePairs.get(DataStore.IDS);
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queueDeleteCollection.call(list);
                if (persist)
                    deleteCollectionGenericsFromCacheAction.call(ids);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
            return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    new JSONObject(keyValuePairs).toString()))
                    .compose(applyExponentialBackoff())
//                .toBlocking()
                    .doOnCompleted(() -> deleteCollectionGenericsFromCacheAction.call(ids))
                    .doOnError(throwable -> {
//                        queueDeleteCollection.call(list);
                        if (persist)
                            deleteCollectionGenericsFromCacheAction.call(ids);
                    });
        });
    }

    @Override
    public Observable<?> dynamicPutObject(String url, HashMap<String, Object> keyValuePairs, Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queuePut.call(object);
                if (persist)
                    saveGenericToCacheAction.call(keyValuePairs);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
            return mRestApi.dynamicPutObject(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    new JSONObject(keyValuePairs).toString()))
//                    .compose(applyExponentialBackoff())
//                .toBlocking()
                    .doOnNext(object -> {
                        if (persist)
                            saveGenericToCacheAction.call(object);
                    })
                    .doOnError(throwable -> {
//                        queuePut.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<List> dynamicPutList(String url, HashMap<String, Object> keyValuePairs, Class domainClass, Class dataClass, boolean persist) {
        mDataClass = dataClass;
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queuePut.call(object);
                if (persist)
                    saveGenericToCacheAction.call(keyValuePairs);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
            return mRestApi.dynamicPutList(url, RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON),
                    new JSONObject(keyValuePairs).toString()))
                    .compose(applyExponentialBackoff())
//                .toBlocking()
                    .doOnNext(saveGenericToCacheAction)
                    .doOnError(throwable -> {
                        if (persist)
                            saveGenericToCacheAction.call(keyValuePairs);
//                        queuePut.call(object);
                    })
                    .map(realmModel -> mEntityDataMapper.transformAllToDomain(realmModel, domainClass));
        });
    }

    @Override
    public Observable<?> dynamicDeleteAll(String url, Class dataClass, boolean persist) {
//        mDataClass = mDataClass;
//        return Observable.defer(() -> {
//            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
//                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
////                queueDeleteAll.call(object);
//                if (persist)
//                    deleteAllGenericsFromCacheAction.call(null);
//                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
//            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
//                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
//                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
//            return mRestApi.dynamicDeleteAll(url)
//       .compose(applyExponentialBackoff())
////                .toBlocking()
//                    .doOnNext(saveGenericToCacheAction)
//                    .doOnError(throwable -> {
//                        if (persist)
//                            deleteAllGenericsFromCacheAction.call(null);
////                         queueDeleteAll.call(object);
//                    });
//        });
        return Observable.error(new Exception("cant delete all from cloud data store"));
    }

    private <T> Observable.Transformer<T, T> applyExponentialBackoff() {
        return observable -> observable.retryWhen(attempts -> {
            ConnectionQuality cq = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
            if (cq.compareTo(ConnectionQuality.MODERATE) >= 0)
                return attempts.zipWith(Observable.range(Constants.COUNTER_START,
                        Constants.ATTEMPTS), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        });
            else return null;
        });
    }

    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant search disk in cloud data store"));
    }

    @Override
    public Observable<List> searchDisk(RealmQuery query, Class domainClass) {
        return Observable.error(new Exception("cant search disk in cloud data store"));
    }
}