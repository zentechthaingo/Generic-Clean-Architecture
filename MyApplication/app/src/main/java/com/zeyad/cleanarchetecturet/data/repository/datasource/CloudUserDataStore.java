package com.zeyad.cleanarchetecturet.data.repository.datasource;

import android.util.Log;

import com.zeyad.cleanarchetecturet.data.cache.RealmManager;
import com.zeyad.cleanarchetecturet.data.entity.UserEntity;
import com.zeyad.cleanarchetecturet.data.entity.UserRealmModel;
import com.zeyad.cleanarchetecturet.data.entity.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchetecturet.data.net.RestApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * {@link UserDataStore} implementation based on connections to the api (Cloud).
 */
public class CloudUserDataStore implements UserDataStore {

    private final RestApi restApi;
    private final RealmManager realmManager;
    private final UserEntityDataMapper userEntityDataMapper;
    private static final int COUNTER_START = 1;
    private static final int ATTEMPTS = 5;
    private String TAG = "CloudUserDataStore";

    private final Action1<UserRealmModel> saveToCacheAction = new Action1<UserRealmModel>() {
        @Override
        public void call(UserRealmModel userRealmModel) {
            if (userRealmModel != null)
                realmManager.put(userRealmModel);
        }
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
        return restApi.userEntityList();
//        .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
//            @Override
//            public Observable<?> call(Observable<? extends Throwable> observable) {
//                Log.v(TAG, "retryWhen, call");
//                return observable.compose(zipWithFlatMap());
//            }
//        }).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
//            @Override
//            public Observable<?> call(Observable<? extends Void> observable) {
//                Log.v(TAG, "repeatWhen, call");
//                return observable.compose(zipWithFlatMap());
//            }
//        });
    }

    @Override
    public Observable<UserEntity> userEntityDetails(final int userId) {
        return restApi.userEntityById(userId).map(new Func1<UserEntity, UserRealmModel>() {
            @Override
            public UserRealmModel call(UserEntity userEntity) {
                return userEntityDataMapper.transformToRealm(userEntity);
            }
        }).doOnNext(saveToCacheAction).map(new Func1<UserRealmModel, UserEntity>() {
            @Override
            public UserEntity call(UserRealmModel userRealmModel) {
                return userEntityDataMapper.transform(userRealmModel);
            }
        });
//        return restApi.userEntityById(userId).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
//            @Override
//            public Observable<?> call(Observable<? extends Throwable> observable) {
//                Log.v(TAG, "retryWhen, call");
//                return observable.compose(zipWithFlatMap());
//            }
//        }).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
//            @Override
//            public Observable<?> call(Observable<? extends Void> observable) {
//                Log.v(TAG, "repeatWhen, call");
//                return observable.compose(zipWithFlatMap());
//            }
//        }).map(new Func1<UserEntity, UserRealmModel>() {
//            @Override
//            public UserRealmModel call(UserEntity userEntity) {
//                return userEntityDataMapper.transformToRealm(userEntity);
//            }
//        }).doOnNext(saveToCacheAction).map(new Func1<UserRealmModel, UserEntity>() {
//            @Override
//            public UserEntity call(UserRealmModel userRealmModel) {
//                return userEntityDataMapper.transform(userRealmModel);
//            }
//        });
    }

    private <T> Observable.Transformer<T, Long> zipWithFlatMap() {
        return new Observable.Transformer<T, Long>() {
            @Override
            public Observable<Long> call(Observable<T> observable) {
                return observable.zipWith(Observable.range(COUNTER_START, ATTEMPTS), new Func2<T, Integer, Integer>() {
                    @Override
                    public Integer call(T t, Integer repeatAttempt) {
                        Log.v(TAG, "zipWith, call, repeatAttempt " + repeatAttempt);
                        return repeatAttempt;
                    }
                }).flatMap(new Func1<Integer, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Integer repeatAttempt) {
                        Log.v(TAG, "flatMap, call, repeatAttempt " + repeatAttempt);
                        // increase the waiting time
                        return Observable.timer(repeatAttempt * 5, TimeUnit.SECONDS);
                    }
                });
            }
        };
    }
}