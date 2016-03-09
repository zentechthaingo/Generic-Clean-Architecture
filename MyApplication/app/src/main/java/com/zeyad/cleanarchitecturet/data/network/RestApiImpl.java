package com.zeyad.cleanarchitecturet.data.network;

import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import java.util.List;

import retrofit.Response;
import retrofit.http.Path;
import rx.Observable;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    public RestApiImpl() {
    }

    @Override
    public Observable<List<UserEntity>> userEntityList() {
        return ApiConnection.userList();
    }

    @Override
    public Observable<List<UserRealmModel>> userRealmList() {
        return ApiConnection.userRealmList();
    }


    @Override
    public Observable<UserEntity> userEntityById(final int userId) {
        return ApiConnection.user(userId);
    }

    @Override
    public Observable<Response> getStream(@Path("index") String index) {
        return ApiConnection.getStream(index);
    }
}