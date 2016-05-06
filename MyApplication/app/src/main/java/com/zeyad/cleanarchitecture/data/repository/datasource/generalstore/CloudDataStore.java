package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.Params;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.jobs.NetworkJob;
import com.zeyad.cleanarchitecture.data.network.RestApi;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.presentation.services.GenericGCMService;
import com.zeyad.cleanarchitecture.presentation.services.GenericJobService;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmObject;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CloudDataStore implements DataStore {

    @Inject
    JobManager jobManager;
    @Inject
    Params params;
    private final RestApi restApi;
    private GeneralRealmManager realmManager;
    private EntityDataMapper entityDataMapper;
    private static final String TAG = "CloudDataStore";
    public static final String POST_TAG = "postObject", DELETE_TAG = "deleteObject", QUEUED_POSTS = "queuedPosts";
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
    // TODO: 6/05/16 Test!
    // TODO: 6/05/16 Finish!
    private Action1<Object> queuePost = object -> {
        Bundle extras = new Bundle();
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.POST);
        extras.putString(GenericNetworkQueueIntentService.POST_OBJECT, new Gson().toJson(object));
        jobManager.start();
        jobManager.addJobInBackground(new NetworkJob(params.addTags(POST_TAG).groupBy(CloudDataStore.QUEUED_POSTS), extras));
        // TODO: 6/05/16 Drop starting here!
        if (Utils.hasLollipop()) {
            if (GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(realmManager.getContext().getApplicationContext())
                    == ConnectionResult.SUCCESS) {
                GcmNetworkManager.getInstance(realmManager.getContext().getApplicationContext().getApplicationContext())
                        .schedule(new OneoffTask.Builder()
                                .setService(GenericGCMService.class)
                                .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                                .setRequiresCharging(false)
                                .setUpdateCurrent(false)
                                .setPersisted(true)
                                .setExtras(extras)
                                .setTag(POST_TAG)
                                .build());
            } else {
                PersistableBundle persistableBundle = new PersistableBundle();
                persistableBundle.putString(GenericNetworkQueueIntentService.POST_OBJECT, new Gson().toJson(object));
                Utils.scheduleJob(realmManager.getContext().getApplicationContext(),
                        new JobInfo.Builder(1,
                                new ComponentName(realmManager.getContext().getApplicationContext(),
                                        GenericJobService.class))
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                .setRequiresCharging(false)
                                .setPersisted(true)
                                .setExtras(persistableBundle)
                                .build());
            }
        }
    };
    // TODO: 6/05/16 Test!
    // TODO: 6/05/16 Finish!
    private final Action1<List> queueDeleteCollection = list -> {
        Bundle extras = new Bundle();
        Gson gson = new Gson();
        ArrayList<String> strings = new ArrayList<>();
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.DELETE_COLLECTION);
        for (Object object : list)
            strings.add(gson.toJson(object));
        extras.putStringArrayList(GenericNetworkQueueIntentService.LIST, strings);
        jobManager.start();
        jobManager.addJobInBackground(new NetworkJob(params.addTags(DELETE_TAG).groupBy(CloudDataStore.QUEUED_POSTS), extras));
        // TODO: 6/05/16 Drop starting here!
        if (Utils.hasLollipop()) {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(realmManager
                    .getContext().getApplicationContext()) == ConnectionResult.SUCCESS) {
                GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(realmManager
                        .getContext().getApplicationContext());
                for (Object object : list) {
                    extras.putString(GenericNetworkQueueIntentService.DELETE_COLLECTION, new Gson().toJson(object));
                    gcmNetworkManager.schedule(new OneoffTask.Builder()
                            .setService(GenericGCMService.class)
                            .setRequiredNetwork(OneoffTask.NETWORK_STATE_ANY)
                            .setRequiresCharging(false)
                            .setExtras(extras)
                            .setPersisted(true)
                            .setUpdateCurrent(false)
                            .setTag(DELETE_TAG)
                            .build());
                    extras.clear();
                }
            } else {
                PersistableBundle persistableBundle = new PersistableBundle();
                for (int i = 0; i < list.size(); i++)
                    persistableBundle.putString(GenericNetworkQueueIntentService.POST_OBJECT + " " + i,
                            gson.toJson(list.get(i)));
                Utils.scheduleJob(realmManager.getContext().getApplicationContext(),
                        new JobInfo.Builder(1,
                                new ComponentName(realmManager.getContext().getApplicationContext(),
                                        GenericJobService.class))
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                .setPersisted(true)
                                .setRequiresCharging(false)
                                .setExtras(persistableBundle)
                                .build());
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
//        return restApi.deleteCollection(list).doOnError(throwable -> queueDeleteCollection.call(list));
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