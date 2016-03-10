package com.zeyad.cleanarchitecturet.data.repository.datasource;

import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecturet.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecturet.data.network.RestApi;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * {@link UserDataStore} implementation based on connections to the api (Cloud).
 */
public class CloudUserDataStore implements UserDataStore {

    private final RestApi restApi;
    private RealmManager realmManager;
    private final UserEntityDataMapper userEntityDataMapper;
    private final String TAG = "CloudUserDataStore";

    List<UserEntity> resultList = new ArrayList<>();
    UserEntity result = new UserEntity();

    private final Action1<UserRealmModel> saveToCacheAction = new Action1<UserRealmModel>() {
        @Override
        public void call(UserRealmModel userRealmModel) {
            if (userRealmModel != null)
                realmManager.put(userRealmModel);
        }
    };
    private final Action1<List<UserRealmModel>> saveAllToCacheAction = userRealmModels -> {
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
    public Observable<List<UserEntity>> userEntityList() {
        restApi.userRealmList()
//                .retryWhen(observable -> {
//                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
//                .repeatWhen(observable -> {
//                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<UserRealmModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<UserRealmModel> userRealmModels) {
                        saveAllToCacheAction.call(userRealmModels);
                        resultList = userEntityDataMapper.transformAll(userRealmModels);
                    }
                });
        return Observable.just(resultList);
//                .compose(Utils.logUsersSource(TAG, realmManager));
    }

    @Override
    public Observable<UserEntity> userEntityDetails(final int userId) {
        restApi.userRealmById(userId)
//                .retryWhen(observable -> {
//                    Log.v(TAG, "retryWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                }).repeatWhen(observable -> {
//                    Log.v(TAG, "repeatWhen, call");
//                    return observable.compose(Utils.zipWithFlatMap(TAG));
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<UserRealmModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(UserRealmModel userRealmModels) {
                        saveToCacheAction.call(userRealmModels);
                        result = userEntityDataMapper.transform(userRealmModels);
                    }
                });
        return Observable.just(result);
//                .compose(Utils.logUserSource("Cloud", realmManager));
    }
}