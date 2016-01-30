package com.zeyad.cleanarchetecturet.data.net;

import com.zeyad.cleanarchetecturet.data.entity.UserEntity;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
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
     * Retrieves an {@link rx.Observable} which will emit a {@link UserEntity}.
     *
     * @param userId The user id used to get user data.
     */
    @GET("user_{id}.json")
    Observable<UserEntity> userEntityById(@Path("id") final int userId);
}