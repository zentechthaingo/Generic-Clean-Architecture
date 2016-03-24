package com.zeyad.cleanarchitecturet.data.network;

import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import java.util.Collection;

import io.realm.RealmObject;
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
     * Retrieves an {@link rx.Observable} which will emit a Collection of {@link UserEntity}.
     */
    // TODO: 3/22/16 deprecate!
    @GET("users.json")
    Observable<Collection<UserEntity>> userEntityCollection();

    @GET("users.json")
    Observable<Collection> userCollection();

    @GET("users.json")
    Observable<Collection<RealmObject>> userRealmObjectCollection();

    @GET("users.json")
    Observable<Collection<UserRealmModel>> userRealmModelCollection();

    /**
     * Retrieves an {@link rx.Observable} which will emit a {@link UserEntity}.
     *
     * @param userId The user id used to get user data.
     */
    // TODO: 3/22/16 deprecate!
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

    @GET("user_{id}.json")
    Observable<RealmObject> realmObjectById(@Path("id") final int userId);

    @GET("user_{id}.json")
    Observable<Object> objectById(@Path("id") final int userId);

    @Streaming
    @GET("/images/{index}.jpg")
    Observable<Response> getStream(@Path("index") String index);
}