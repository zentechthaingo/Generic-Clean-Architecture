package com.zeyad.cleanarchitecture.data.repository.datastore;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.exceptions.NetworkConnectionException;
import com.zeyad.cleanarchitecture.data.network.RestApi;
import com.zeyad.cleanarchitecture.presentation.services.GenericGCMService;
import com.zeyad.cleanarchitecture.presentation.services.GenericJobService;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class CloudDataStore implements DataStore {

    private Gson mGson;
    private Context mContext;
    private final RestApi mRestApi;
    private GeneralRealmManager mRealmManager;
    private EntityMapper mEntityDataMapper;
    private static final String TAG = CloudDataStore.class.getName();
    private Class dataClass;
    private final Action1<Object> saveGenericToCacheAction =
            object -> mRealmManager.put((RealmObject) mEntityDataMapper.transformToRealm(object, dataClass))
                    .subscribeOn(Schedulers.io())
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
                            Log.d(TAG, object.getClass().getName() + " added!");
                        }
                    });
    private final Action1<List> saveAllGenericsToCacheAction = collection -> {
        List<RealmObject> realmObjectCollection = new ArrayList<>();
        realmObjectCollection.addAll(mEntityDataMapper.transformAllToRealm(collection, dataClass));
        mRealmManager.putAll(realmObjectCollection);
    };
    private final Action1<List> deleteCollectionGenericsFromCacheAction = collection -> {
        List<RealmObject> realmObjectList = new ArrayList<>();
        realmObjectList.addAll(mEntityDataMapper.transformAllToRealm(collection, dataClass));
        for (RealmObject realmObject : realmObjectList)
            mRealmManager.evict(realmObject, dataClass);
    };
    // TODO: 6/05/16 Test!
    private Func1<Object, Boolean> queuePost = object -> {
        Bundle extras = new Bundle();
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.POST);
        extras.putString(GenericNetworkQueueIntentService.POST_OBJECT, mGson.toJson(object));
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
            GcmNetworkManager.getInstance(mContext).schedule(new OneoffTask.Builder()
                    .setService(GenericGCMService.class)
                    .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .setUpdateCurrent(false)
                    .setPersisted(true)
                    .setExtras(extras)
                    .setTag(Constants.POST_TAG)
                    .build());
            Log.d(TAG, "queuePost scheduled through GcmNetworkManager: " + true);
            return true;
        } else if (Utils.hasLollipop()) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString(GenericNetworkQueueIntentService.POST_OBJECT, mGson.toJson(object));
            boolean isScheduled = Utils.scheduleJob(mContext, new JobInfo.Builder(1, new ComponentName(mContext,
                    GenericJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setExtras(persistableBundle)
                    .build());
            Log.d(TAG, "queuePost scheduled through JobScheduler: " + isScheduled);
            return isScheduled;
        }
        return false;
    };
    // TODO: 6/05/16 Test!
    private final Func1<List, Boolean> queueDeleteCollection = list -> {
        Bundle extras = new Bundle();
        ArrayList<String> strings = new ArrayList<>();
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.DELETE_COLLECTION);
        for (Object object : list)
            strings.add(mGson.toJson(object));
        extras.putStringArrayList(GenericNetworkQueueIntentService.LIST, strings);
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
            GcmNetworkManager.getInstance(mContext).schedule(new OneoffTask.Builder()
                    .setService(GenericGCMService.class)
                    .setRequiredNetwork(OneoffTask.NETWORK_STATE_ANY)
                    .setRequiresCharging(false)
                    .setExtras(extras)
                    .setPersisted(true)
                    .setUpdateCurrent(false)
                    .setTag(Constants.DELETE_TAG)
                    .build());
            Log.d(TAG, "queueDeleteCollection scheduled through GcmNetworkManager: " + true);
            return true;
        } else if (Utils.hasLollipop()) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putStringArray(GenericNetworkQueueIntentService.LIST, (String[]) strings.toArray());
            boolean isScheduled = Utils.scheduleJob(mContext, new JobInfo.Builder(1, new ComponentName(mContext, GenericJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setRequiresCharging(false)
                    .setExtras(persistableBundle)
                    .build());
            Log.d(TAG, "queueDeleteCollection scheduled through JobScheduler: " + isScheduled);
            return isScheduled;
        }
        return false;
    };

    /**
     * Construct a {@link DataStore} based on connections to the api (Cloud).
     *
     * @param mRestApi      The {@link RestApi} implementation to use.
     * @param mRealmManager A {@link GeneralRealmManager} to cache data retrieved from the api.
     */
    public CloudDataStore(RestApi mRestApi, GeneralRealmManager mRealmManager, EntityMapper mEntityDataMapper) {
        this.mRestApi = mRestApi;
        this.mEntityDataMapper = mEntityDataMapper;
        this.mRealmManager = mRealmManager;
        mContext = mRealmManager.getContext().getApplicationContext();
        mGson = new Gson();
    }

    @Override
    public Observable<List> collection(Class domainClass, Class dataClass, boolean persist) {
        this.dataClass = dataClass;
        return mRestApi.userCollection()
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        }))
//                .toBlocking()
                .doOnNext(list -> {
                    if (persist)
                        saveAllGenericsToCacheAction.call(list);
                })
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels, domainClass));
    }

    @Override
    public Observable<?> getById(final int itemId, Class domainClass, Class dataClass, boolean persist) {
        this.dataClass = dataClass;
        return mRestApi.objectById(itemId)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        }))
//                .toBlocking()
                .doOnNext(object -> {
                    if (persist)
                        saveGenericToCacheAction.call(object);
                })
                .map(entity -> mEntityDataMapper.transformToDomain(entity, domainClass));
    }

    @Override
    public Observable<List> dynamicList(String url, Class domainClass, Class dataClass, boolean persist) {
        this.dataClass = dataClass;
        return mRestApi.dynamicGetList(url)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        }))
//                .toBlocking()
                .doOnNext(list -> {
                    if (persist)
                        saveAllGenericsToCacheAction.call(list);
                })
                .map(entities -> mEntityDataMapper.transformAllToDomain(entities, domainClass));
    }

    @Override
    public Observable<?> dynamicObject(String url, Class domainClass, Class dataClass, boolean persist) {
        this.dataClass = dataClass;
        return mRestApi.dynamicGetObject(url)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        }))
//                .toBlocking()
                .doOnNext(object -> {
                    if (persist)
                        saveGenericToCacheAction.call(object);
                })
                .map(entity -> mEntityDataMapper.transformToDomain(entity, domainClass));
    }

    @Override
    public Observable<?> dynamicPost(final String url, final HashMap<String, Object> keyValuePairs,
                                     Class domainClass) {
        return mRestApi.dynamicPostObject(url, RequestBody.create(MediaType.parse("application/json"),
                new JSONObject(keyValuePairs).toString()))
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        }))
                .map(entity -> mEntityDataMapper.transformToDomain(entity, domainClass));
    }

    @Override
    public Observable<List> search(String query, String column, Class domainClass, Class dataClass) {
        return mRestApi.search(query)
                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
                        .flatMap(i -> {
                            Log.d(TAG, "delay retry by " + i + " second(s)");
                            return Observable.timer(i, TimeUnit.SECONDS);
                        }))
//                .toBlocking()
                .map(realmModels -> mEntityDataMapper.transformAllToDomain(realmModels, domainClass));
    }

    @Override
    public Observable<?> putToCloud(Object object, Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queuePost.call(object);
                if (persist)
                    saveGenericToCacheAction.call(object);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
//            return mRestApi.postItem(object)
//                    .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
//                            .flatMap(i -> {
//                                Log.d(TAG, "delay retry by " + i + " second(s)");
//                                return Observable.timer(i, TimeUnit.SECONDS);
//                            }))
////                .toBlocking()
//                    .doOnNext(saveGenericToCacheAction)
//                    .doOnError(throwable -> {
//                        if (persist)
//                            saveGenericToCacheAction.call(object);
////                        queuePost.call(object);
//                    })
//                    .map(realmModel -> mEntityDataMapper.transformToDomain(realmModel, domainClass));
            return Observable.just(true);
        });
    }

    @Override
    public Observable<?> deleteCollectionFromCloud(List list, Class domainClass, Class dataClass, boolean persist) {
        return Observable.defer(() -> {
            if (!Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//                queueDeleteCollection.call(list);
                if (persist)
                    deleteCollectionGenericsFromCacheAction.call(list);
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_persisted)));
            } else if (!Utils.isNetworkAvailable(mContext) && !(Utils.hasLollipop()
                    || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS))
                return Observable.error(new NetworkConnectionException(mContext.getString(R.string.network_error_not_persisted)));
//            return mRestApi.deleteCollection(list)
//                    .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i)
//                            .flatMap(i -> {
//                                Log.d(TAG, "delay retry by " + i + " second(s)");
//                                return Observable.timer(i, TimeUnit.SECONDS);
//                            }))
////                .toBlocking()
//                    .doOnCompleted(() -> deleteCollectionGenericsFromCacheAction.call(list))
//                    .doOnError(throwable -> {
////                        queueDeleteCollection.call(list);
//                        if (persist)
//                            deleteCollectionGenericsFromCacheAction.call(list);
//                    });
            return Observable.just(true);
        });
    }

    @Override
    public Observable<?> putToDisk(RealmObject object, Class dataClass) {
        return Observable.error(new Exception("cant put to disk in cloud data store"));
    }

    @Override
    public Observable<?> putToDisk(Object object, Class dataClass) {
        return Observable.error(new Exception("cant put to disk in cloud data store"));
    }

    @Override
    public Observable<?> deleteCollectionFromDisk(List list, Class dataClass) {
        return Observable.error(new Exception("cant delete from disk in cloud data store"));
    }
}