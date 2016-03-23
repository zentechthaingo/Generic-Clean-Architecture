package com.zeyad.cleanarchitecturet.data.repository.datasource.userstore;

import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecturet.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecturet.data.network.RestApi;
import com.zeyad.cleanarchitecturet.utilities.Utils;

import java.util.Collection;

import rx.Observable;
import rx.functions.Action1;

/**
 * {@link UserDataStore} implementation based on connections to the api (Cloud).
 */
public class CloudUserDataStore implements UserDataStore {

    private final RestApi restApi;
    private RealmManager realmManager;
    private final UserEntityDataMapper userEntityDataMapper;
    private final String TAG = "CloudUserDataStore";
    private final Action1<UserRealmModel> saveToCacheAction = new Action1<UserRealmModel>() {
        @Override
        public void call(UserRealmModel userRealmModel) {
            if (userRealmModel != null)
                realmManager.put(userRealmModel);
        }
    };
    private final Action1<Collection<UserRealmModel>> saveAllToCacheAction = userRealmModels -> {
        if (userRealmModels != null)
            realmManager.putAll(userRealmModels);
    };

    /**
     * Construct a {@link UserDataStore} based on connections to the api (Cloud).
     *
     * @param restApi      The {@link RestApi} implementation to use.
     * @param realmManager A {@link RealmManager} to cache data retrieved from the api.
     */
    public CloudUserDataStore(RestApi restApi, RealmManager realmManager, UserEntityDataMapper userEntityDataMapper) {
        this.restApi = restApi;
        this.userEntityDataMapper = userEntityDataMapper;
        this.realmManager = realmManager;
    }

    @Override
    public Observable<Collection<UserEntity>> userEntityList() {
        return restApi.userRealmModelCollection()
//                .retryWhen(observable -> {
//                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                }).repeatWhen(observable -> {
//                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
                .doOnNext(saveAllToCacheAction::call)
                .map(userEntityDataMapper::transformAllFromRealm)
                .compose(Utils.logUsersSources(TAG, realmManager));
    }

    @Override
    public Observable<UserEntity> userEntityDetails(final int userId) {
        return restApi.userRealmById(userId)
//                .retryWhen(observable -> {
////                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                }).repeatWhen(observable -> {
////                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
                .doOnNext(saveToCacheAction::call)
                .map(userEntityDataMapper::transform)
                .compose(Utils.logUserSource(TAG, realmManager));
    }
}