package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.domain.services.ImageDownloadGcmService;
import com.zeyad.cleanarchitecture.domain.services.ImageDownloadIntentService;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.Collection;

import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action1;

public class DiskDataStore implements DataStore {

    private GeneralRealmManager realmManager;
    //    private RealmRepository realmRepository;
    public final String TAG = "DiskUserDataStore", POST_TAG = "postObject", DELETE_TAG = "delete",
            DELETE_BY_ID_TAG = "deleteById";
    private Action1<Object> queuePost = object -> {
        if (Utils.hasLollipop()) {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(realmManager.getContext())
                    == ConnectionResult.SUCCESS) {
                Bundle extras = new Bundle();
                extras.putString(ImageDownloadIntentService.POST_OBJECT, new Gson().toJson(object));
                GcmNetworkManager.getInstance(realmManager.getContext()).schedule(new OneoffTask.Builder()
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
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(realmManager.getContext())
                    == ConnectionResult.SUCCESS) {
                Bundle extras = new Bundle();
                extras.putInt(ImageDownloadIntentService.DELETE_OBJECT, integer);
                GcmNetworkManager.getInstance(realmManager.getContext()).schedule(new OneoffTask.Builder()
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
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(realmManager.getContext())
                    == ConnectionResult.SUCCESS) {
                Bundle extras = new Bundle();
                extras.putString(ImageDownloadIntentService.POST_OBJECT, new Gson().toJson(object));
                GcmNetworkManager.getInstance(realmManager.getContext()).schedule(new OneoffTask.Builder()
                        .setService(ImageDownloadGcmService.class)
                        .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                        .setExtras(extras)
                        .setTag(DELETE_TAG)
                        .build()); // gcm service
            }
        }
    };

    /**
     * Construct a {@link UserDataStore} based file system data store.
     *
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public DiskDataStore(GeneralRealmManager realmManager) {
        this.realmManager = realmManager;
    }

    @Override
    public Observable<Collection> entityListFromDisk(Class clazz) {
        return realmManager.getAll(clazz).compose(Utils.logSources(TAG, realmManager));
    }

    @Override
    public Observable<?> entityDetailsFromDisk(final int itemId, Class clazz) {
        return realmManager.get(itemId, clazz).compose(Utils.logSource(TAG, realmManager));
    }

    @Override
    public Observable<?> putToDisk(RealmObject object) {
        return Observable.create(subscriber -> {
            realmManager.put(object);
            queuePost.call(object);
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<?> deleteFromDisk(int itemId, Class clazz) {
        return Observable.create(subscriber -> {
            realmManager.evictById(itemId, clazz);
            queueDeleteById.call(itemId);
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<?> deleteFromDisk(Object realmObject, Class clazz) {
        return Observable.create(subscriber -> {
            realmManager.evict(((RealmObject) realmObject), clazz);
            queueDelete.call(realmObject);
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<?> deleteCollectionFromDisk(Collection collection, Class clazz) {
        return Observable.create(subscriber -> {
            realmManager.evictCollection(collection, clazz);
            for (Object object : collection)
                queueDelete.call(object);
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Collection> collectionFromCloud(Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> entityDetailsFromCloud(int itemId, Class domainClass, Class
            dataClass) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> postToCloud(Object object) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteFromCloud(Object realmObject, Class clazz) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteCollectionFromCloud(Collection collection, Class clazz) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteFromCloud(int itemId, Class clazz) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }
    //--------------------------------------------------------------------------------------------//

//    public Observable store(Class clazz, Object object) {
//        try {
//            realmRepository.storeObject(clazz, new JSONObject(new Gson().toJson(object, clazz)));
//            return Observable.empty();
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return Observable.error(e);
//        }
//    }
//
//    public Observable<?> getById(Class clazz, String column, final int id) {
//        return realmRepository.get(clazz, predicate -> predicate.equalTo(column, id));
//    }
}