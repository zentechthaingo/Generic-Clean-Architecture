package com.zeyad.cleanarchetecturet.data.repository.datasource;

import android.util.Log;

import com.zeyad.cleanarchetecturet.data.cache.UserCache;
import com.zeyad.cleanarchetecturet.data.entity.UserEntity;
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
    private final UserCache userCache;
    private static final int COUNTER_START = 1;
    private static final int ATTEMPTS = 5;
    private String TAG = "CloudUserDataStore";

    private final Action1<UserEntity> saveToCacheAction = new Action1<UserEntity>() {
        @Override
        public void call(UserEntity userEntity) {
            if (userEntity != null) {
                userCache.put(userEntity);
            }
        }
    };

    /**
     * Construct a {@link UserDataStore} based on connections to the api (Cloud).
     *
     * @param restApi   The {@link RestApi} implementation to use.
     * @param userCache A {@link UserCache} to cache data retrieved from the api.
     */
    public CloudUserDataStore(RestApi restApi, UserCache userCache) {
        this.restApi = restApi;
        this.userCache = userCache;
    }

    @Override
    public Observable<List<UserEntity>> userEntityList() {
        return retryWhenRepeatWhen(restApi.userEntityList());
//                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
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
        return restApi.userEntityById(userId).doOnNext(saveToCacheAction);
    }

    private <T> Observable retryWhenRepeatWhen(Observable <T> observable){
        return observable.retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Throwable> observable) {
                Log.v(TAG, "retryWhen, call");
                return observable.compose(zipWithFlatMap());
            }
        }).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
            @Override
            public Observable<?> call(Observable<? extends Void> observable) {
                Log.v(TAG, "repeatWhen, call");
                return observable.compose(zipWithFlatMap());
            }
        });
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