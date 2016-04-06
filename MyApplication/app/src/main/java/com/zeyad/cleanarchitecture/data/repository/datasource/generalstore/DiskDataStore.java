package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.Collection;

import io.realm.RealmObject;
import rx.Observable;

public class DiskDataStore implements DataStore {

    private GeneralRealmManager realmManager;
    //    private RealmRepository realmRepository;
    public final String TAG = "DiskUserDataStore";

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
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(object);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<?> deleteFromDisk(int itemId, Class clazz) {
        return Observable.create(subscriber -> {
            realmManager.evictById(itemId, clazz);
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
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Collection> searchDisk(String query, Class clazz) {
        return Observable.create(subscriber -> {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(realmManager.getWhere(query, clazz));
                subscriber.onCompleted();
            }
        });
    }

//    @Override
//    public Observable<Object> searchDisk(String query, Class clazz) {
//        return Observable.create(subscriber -> {
//            for (Object userRealmModel :
//                    realmManager.getWhere(query, clazz))
//                if (!subscriber.isUnsubscribed())
//                    subscriber.onNext(userRealmModel);
//            if (!subscriber.isUnsubscribed())
//                subscriber.onCompleted();
//        });
//    }

    @Override
    public Observable<Collection> searchCloud(String query, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
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
    public Observable<?> postToCloud(Object object, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteFromCloud(Object realmObject, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteCollectionFromCloud(Collection collection, Class domainClass, Class dataClass) {
        return Observable.error(new Exception("cant get from cloud in disk data store"));
    }

    @Override
    public Observable<?> deleteFromCloud(int itemId, Class domainClass, Class dataClass) {
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