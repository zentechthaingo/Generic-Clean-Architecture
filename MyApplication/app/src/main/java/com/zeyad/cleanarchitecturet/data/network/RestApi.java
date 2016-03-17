package com.zeyad.cleanarchitecturet.data.network;

import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import java.util.List;

import retrofit.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Streaming;
import rx.Observable;

/**
 * RestApi for retrieving data from the network.
 */
public interface RestApi {
    String API_BASE_URL = "http://www.android10.org/myapi/";

    /**
     * Retrieves an {@link rx.Observable} which will emit a List of {@link UserEntity}.
     */
    @GET("users.json")
    Observable<List<UserEntity>> userEntityList();

    /**
     * Retrieves an {@link rx.Observable} which will emit a List of {@link UserEntity}.
     */
    @GET("users.json")
    Observable<List<?>> userList();

    @GET("users.json")
    Observable<List<UserRealmModel>> userRealmList();

    /**
     * Retrieves an {@link rx.Observable} which will emit a {@link UserEntity}.
     *
     * @param userId The user id used to get user data.
     */
    @GET("user_{id}.json")
    Observable<UserEntity> userEntityById(@Path("id") final int userId);

    /**
     * Retrieves an {@link rx.Observable} which will emit a {@link UserEntity}.
     *
     * @param userId The user id used to get user data.
     */
    @GET("user_{id}.json")
    Observable<?> userById(@Path("id") final int userId);

    @GET("user_{id}.json")
    Observable<UserRealmModel> userRealmById(@Path("id") final int userId);

    @Streaming
    @GET("/images/{index}.jpg")
    Observable<Response> getStream(@Path("index") String index);
}