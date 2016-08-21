package com.zeyad.cleanarchitecture.data.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Part;
import retrofit2.http.Url;
import rx.Observable;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    public RestApiImpl() {
    }

    @Override
    public Observable<ResponseBody> dynamicDownload(@Url String url) {
        return ApiConnection.dynamicDownload(url);
    }

    @Override
    public Observable<ResponseBody> upload(@Url String url, @Part("description") RequestBody description,
                                           @Part MultipartBody.Part file) {
        return ApiConnection.upload(url, file);
    }

    @Override
    public Observable<ResponseBody> upload(@Url String url, @Part("description") RequestBody description,
                                           @Part MultipartBody.Part file, boolean shouldCache) {
        return ApiConnection.upload(url, file);
    }

    @Override
    public Observable<Object> upload(@Url String url, @Part(value = "image", encoding = "binary") RequestBody requestBody) {
        return ApiConnection.upload(url, requestBody);
    }

    @Override
    public Observable<Object> upload(@Url String url, @Part(value = "file\";filename=\"somename.jpg\"",
            encoding = "binary") RequestBody description, boolean shouldCache) {
        return ApiConnection.upload(url, description);
    }

    @Override
    public Observable<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file, boolean shouldCache) {
        return ApiConnection.upload(url, file);
    }

    @Override
    public Observable<Object> dynamicGetObject(@Url String url) {
        return ApiConnection.dynamicGetObject(url);
    }

    @Override
    public Observable<Object> dynamicGetObject(@Url String url, boolean shouldCache) {
        return ApiConnection.dynamicGetObject(url, shouldCache);
    }

    @Override
    public Observable<List> dynamicGetList(@Url String url) {
        return ApiConnection.dynamicGetList(url);
    }

    @Override
    public Observable<List> dynamicGetList(@Url String url, boolean shouldCache) {
        return ApiConnection.dynamicGetList(url, shouldCache);
    }

    @Override
    public Observable<Object> dynamicPostObject(@Url String url, RequestBody body) {
        return ApiConnection.dynamicPostObject(url, body);
    }

    @Override
    public Observable<List> dynamicPostList(@Url String url, @Body RequestBody body) {
        return ApiConnection.dynamicPostList(url, body);
    }

    @Override
    public Observable<Object> dynamicPutObject(@Url String url, @Body RequestBody body) {
        return ApiConnection.dynamicPutObject(url, body);
    }

    @Override
    public Observable<List> dynamicPutList(@Url String url, @Body RequestBody body) {
        return ApiConnection.dynamicPutList(url, body);
    }

    @Override
    public Observable<Object> dynamicDeleteObject(@Url String url, @Body RequestBody body) {
        return ApiConnection.dynamicDeleteObject(url, body);
    }

    @Override
    public Observable<List> dynamicDeleteList(@Url String url, @Body RequestBody body) {
        return ApiConnection.dynamicDeleteList(url, body);
    }

//    @Override
//    public Call<RefreshTokenEntity> refreshToken(@Url String url, @Body RequestBody body) {
//        return ApiConnection.refreshToken(url, body);
//    }

    @Override
    public Observable<ResponseBody> upload(@Url String url, @Part MultipartBody.Part file) {
        return ApiConnection.upload(url, file);
    }
}