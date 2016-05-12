package com.zeyad.cleanarchitecture.data.network;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import java.util.Collection;
import java.util.List;

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

    @GET
    Observable<Object> dynamicGetObject(@Url String url);

    @GET
    Observable<List> dynamicGetList(@Url String url);

//    @POST
//    Observable<Object> dynamicPostList(@Url String url);

    /**
     * Retrieves an {@link rx.Observable} which will emit a collection of {@link UserEntity}.
     */
    @GET("users.json")
    Observable<List> userCollection();

    // TODO: 10/05/16 remove!
    @GET("users.json")
    Observable<List<UserRealmModel>> userRealmModelCollection();

    /**
     * Retrieves an {@link rx.Observable} which will emit a {@link UserEntity}.
     *
     * @param userId The user id used to getById user data.
     */
    // TODO: 10/05/16 remove!
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
    Observable<List> search(@Path("query") String query);

    @Streaming
    @GET("cover_{index}.jpg")
    Observable<ResponseBody> download(@Path("index") int index);

    @Streaming
    @GET
    Observable<ResponseBody> dynamicDownload(@Url String fileUrl);

    @Multipart
    @POST("upload")
    Observable<ResponseBody> upload(@Part("description") RequestBody description,
                                    @Part MultipartBody.Part file);
}