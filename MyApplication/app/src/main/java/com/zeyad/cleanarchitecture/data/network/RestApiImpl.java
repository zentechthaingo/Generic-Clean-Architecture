package com.zeyad.cleanarchitecture.data.network;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import java.util.Collection;

import retrofit2.Response;
import retrofit2.http.Path;
import rx.Observable;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    public RestApiImpl() {
    }

    @Override
    public Observable<Collection> userCollection() {
        return ApiConnection.userCollection();
    }


    @Override
    public Observable<Collection<UserRealmModel>> userRealmModelCollection() {
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
    public Observable<Response> getStream(@Path("index") String index) {
        return ApiConnection.getStream(index);
    }
}