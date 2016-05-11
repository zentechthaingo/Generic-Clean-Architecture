package com.zeyad.cleanarchitecture.data.network;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import java.util.Collection;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    public RestApiImpl() {
    }

    @Override
    public Observable<Object> dynamicGetObject(@Url String url) {
        return ApiConnection.dynamicGetObject(url);
    }

    @Override
    public Observable<List> dynamicGetList(@Url String url) {
        return ApiConnection.dynamicGetList(url);
    }

    @Override
    public Observable<List> userCollection() {
        return ApiConnection.userCollection();
    }

    @Override
    public Observable<List<UserRealmModel>> userRealmModelCollection() {
        return ApiConnection.userRealmCollection();
    }

    @Override
    public Observable<UserRealmModel> userRealmById(@Path("id") int userId) {
        return ApiConnection.userRealm(userId);
    }

    @Override
    public Observable<Object> objectById(@Path("id") int userId) {
        return ApiConnection.objectById(userId);
    }

    @Override
    public Observable<Object> deleteItemById(@Path("id") int userId) {
        return ApiConnection.deleteItemById(userId);
    }

    @Override
    public Observable<Object> deleteItem(@Path("object") Object object) {
        return ApiConnection.deleteItem(object);
    }

    @Override
    public Observable<Object> deleteCollection(@Path("collection") Collection collection) {
        return ApiConnection.deleteCollection(collection);
    }

    @Override
    public Observable<Object> postItem(@Path("id") Object object) {
        return ApiConnection.postItem(object);
    }

    @Override
    public Observable<List> search(@Path("query") String query) {
        return null;
    }

    @Override
    public Observable<ResponseBody> download(@Path("index") int index) {
        return ApiConnection.download(index);
    }

    @Override
    public Observable<ResponseBody> dynamicDownload(@Url String url) {
        return ApiConnection.dynamicDownload(url);
    }

    @Override
    public Observable<ResponseBody> upload(@Part("description") RequestBody description, @Part MultipartBody.Part file) {
        return ApiConnection.upload(file, description);
    }
}