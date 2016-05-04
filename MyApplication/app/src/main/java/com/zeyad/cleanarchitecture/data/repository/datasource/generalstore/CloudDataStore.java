package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.network.RestApi;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.domain.services.ImageDownloadGcmService;
import com.zeyad.cleanarchitecture.domain.services.ImageDownloadIntentService;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.RealmObject;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CloudDataStore implements DataStore {

    private final RestApi restApi;
    private GeneralRealmManager realmManager;
    private EntityDataMapper entityDataMapper;
    private static final String TAG = "CloudDataStore", POST_TAG = "postObject", DELETE_TAG = "delete",
            DELETE_BY_ID_TAG = "deleteById";
    private Class dataClass;
    private final Action1<Object> saveGenericToCacheAction =
            object -> realmManager.put((RealmObject) entityDataMapper.transformToRealm(object, dataClass))
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
        realmObjectCollection.addAll((List) entityDataMapper.transformAllToRealm(collection, dataClass));
        realmManager.putAll(realmObjectCollection);
    };
    private Action1<Object> queuePost = object -> {
        if (Utils.hasLollipop()) {
            if (GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(realmManager.getContext().getApplicationContext())
                    == ConnectionResult.SUCCESS) {
                Bundle extras = new Bundle();
                extras.putString(ImageDownloadIntentService.POST_OBJECT, new Gson().toJson(object));
                GcmNetworkManager.getInstance(realmManager.getContext().getApplicationContext().getApplicationContext())
                        .schedule(new OneoffTask.Builder()
                                .setService(ImageDownloadGcmService.class)
                                .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                                .setExtras(extras)
                                .setTag(POST_TAG)
                                .build()); // gcm service
            }
        }
    };
    private final Action1<Integer> queueDeleteById = integer -> {
        if (Utils.hasLollipop()) {
            if (GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(realmManager.getContext().getApplicationContext())
                    == ConnectionResult.SUCCESS) {
                Bundle extras = new Bundle();
                extras.putInt(ImageDownloadIntentService.DELETE_OBJECT, integer);
                GcmNetworkManager.getInstance(realmManager.getContext().getApplicationContext())
                        .schedule(new OneoffTask.Builder()
                                .setService(ImageDownloadGcmService.class)
                                .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                                .setExtras(extras)
                                .setTag(DELETE_BY_ID_TAG)
                                .build()); // gcm service
            }
        }
    };
    private final Action1<Object> queueDelete = object -> {
        if (Utils.hasLollipop()) {
            if (GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(realmManager.getContext().getApplicationContext())
                    == ConnectionResult.SUCCESS) {
                Bundle extras = new Bundle();
                extras.putString(ImageDownloadIntentService.POST_OBJECT, new Gson().toJson(object));
                GcmNetworkManager.getInstance(realmManager.getContext().getApplicationContext())
                        .schedule(new OneoffTask.Builder()
                                .setService(ImageDownloadGcmService.class)
                                .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                                .setExtras(extras)
                                .setTag(DELETE_TAG)
                                .build()); // gcm service
            }
        }
    };
    private final Action1<Collection> queueDeleteCollection = collection -> {
        if (Utils.hasLollipop()) {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(realmManager.getContext().getApplicationContext())
                    == ConnectionResult.SUCCESS) {
                GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(realmManager.getContext().getApplicationContext());
                Bundle extras = new Bundle();
                for (Object object : collection) {
                    extras.putString(ImageDownloadIntentService.POST_OBJECT, new Gson().toJson(object));
                    gcmNetworkManager.schedule(new OneoffTask.Builder()
                            .setService(ImageDownloadGcmService.class)
                            .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                            .setExtras(extras)
                            .setTag(DELETE_TAG)
                            .build()); // gcm service
                    extras.clear();
                }
            }
        }
    };

    /**
     * Construct a {@link UserDataStore} based on connections to the api (Cloud).
     *
     * @param restApi      The {@link RestApi} implementation to use.
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public CloudDataStore(RestApi restApi, GeneralRealmManager realmManager, EntityDataMapper entityDataMapper) {
        this.restApi = restApi;
        this.entityDataMapper = entityDataMapper;
        this.realmManager = realmManager;
    }

    @Override
    public Observable<List> collection(Class domainClass, Class dataClass) {
        this.dataClass = dataClass;
        return restApi.userCollection()
//                .retryWhen(observable -> {
//                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                }).repeatWhen(observable -> {
//                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
                .doOnNext(saveAllGenericsToCacheAction)
                .map(realmModels -> entityDataMapper.transformAllToDomain(realmModels, domainClass));
    }

    @Override
    public Observable<?> getById(final int itemId, Class domainClass, Class dataClass) {
        this.dataClass = dataClass;
        return restApi.objectById(itemId)
//                .retryWhen(observable -> {
//                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                }).repeatWhen(observable -> {
//                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
                .doOnNext(saveGenericToCacheAction)
                .map(entities -> entityDataMapper.transformToDomain(entities, domainClass));
    }

    @Override
    public Observable<?> postToCloud(Object object, Class domainClass, Class dataClass) {
//        return restApi.postItem(object)
//                .doOnNext(saveGenericToCacheAction)
//                .doOnError(throwable -> queuePost.call(object))
//                .map(realmModel -> entityDataMapper.transformToDomain(realmModel, domainClass));
        return Observable.just(true);
    }

    @Override
    public Observable<List> searchCloud(String query, Class domainClass, Class dataClass) {
//        return restApi.search(query).map(realmModels -> entityDataMapper.transformAllToDomain(realmModels, domainClass));
        return Observable.empty();
    }

    @Override
    public Observable<?> deleteCollectionFromCloud(List list, Class domainClass, Class dataClass) {
//        return restApi.deleteCollection(collection).doOnError(throwable -> queueDeleteCollection.call(list));
        return Observable.just(true);
    }

    @Override
    public Observable<?> putToDisk(RealmObject object) {
        return Observable.error(new Exception("cant getById from disk in cloud data store"));
    }

    @Override
    public Observable<?> putToDisk(Object object, Class dataClass) {
        return Observable.error(new Exception("cant getById from disk in cloud data store"));
    }

    @Override
    public Observable<?> deleteCollectionFromDisk(List list, Class clazz) {
        return Observable.error(new Exception("cant getById from disk in cloud data store"));
    }

    @Override
    public Observable<List> searchDisk(String query, String column, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant getById from disk in cloud data store"));
    }
}