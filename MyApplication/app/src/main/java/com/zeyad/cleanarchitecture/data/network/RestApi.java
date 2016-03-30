package com.zeyad.cleanarchitecture.data.network;

import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import java.util.Collection;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * RestApi for retrieving data from the network.
 */
public interface RestApi {
    String API_BASE_URL = "http://www.android10.org/myapi/";

    /**
     * Retrieves an {@link rx.Observable} which will emit a Collection of {@link UserEntity}.
     */
    @GET("users.json")
    Observable<Collection> userCollection();

    @GET("users.json")
    Observable<Collection<UserRealmModel>> userRealmModelCollection();

    /**
     * Retrieves an {@link rx.Observable} which will emit a {@link UserEntity}.
     *
     * @param userId The user id used to get user data.
     */
    @GET("user_{id}.json")
    Observable<UserRealmModel> userRealmById(@Path("id") final int userId);

    @GET("user_{id}.json")
    Observable<Object> objectById(@Path("id") final int userId);

    @Streaming
    @GET("/images/{index}.jpg")
    Observable<Response> getStream(@Path("index") String index);
}