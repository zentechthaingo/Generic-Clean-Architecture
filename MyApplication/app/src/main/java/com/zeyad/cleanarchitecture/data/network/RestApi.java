package com.zeyad.cleanarchitecture.data.network;

import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import java.util.Collection;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
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
     * @param userId The user id used to getById user data.
     */
    @GET("user_{id}.json")
    Observable<UserRealmModel> userRealmById(@Path("id") final int userId);

    @GET("user_{id}.json")
    Observable<Object> objectById(@Path("id") final int userId);

    //------------------------------------------------------------------------------------//
    @POST("user_{id}.json")
    Observable<Object> deleteItemById(@Path("id") final int userId);

    @POST("user_{id}.json")
    Observable<Object> deleteItem(@Path("object") final Object object);

    @POST("user_{id}.json")
    Observable<Object> deleteCollection(@Path("collection") final Collection collection);

    @POST("user_{id}.json")
    Observable<Object> postItem(@Path("id") final Object object);

    @GET("user_{id}.json")
    Observable<Collection> search(@Path("query") String query);

    @Streaming
    @GET("/images/{index}.jpg")
    Observable<ResponseBody> download(@Path("index") String index);

    @Streaming
    @GET
    Observable<ResponseBody> dynamicDownload(@Url String fileUrl);

    @Multipart
    @POST("upload")
    Observable<ResponseBody> upload(@Part("description") RequestBody description, @Part MultipartBody.Part file);

}