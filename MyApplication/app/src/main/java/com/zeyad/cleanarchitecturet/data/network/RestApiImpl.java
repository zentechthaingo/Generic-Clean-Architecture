package com.zeyad.cleanarchitecturet.data.network;

import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;

import java.util.Collection;

import io.realm.RealmObject;
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
    public Observable<Collection<UserEntity>> userEntityCollection() {
        return ApiConnection.userEntityCollection();
    }

    @Override
    public Observable<Collection> userCollection() {
        return ApiConnection.userCollection();
    }

    @Override
    public Observable<Collection<RealmObject>> userRealmObjectCollection() {
        return ApiConnection.realmCollection();
    }

    @Override
    public Observable<Collection<UserRealmModel>> userRealmModelCollection() {
        return ApiConnection.userRealmCollection();
    }

    @Override
    public Observable<UserEntity> userEntityById(final int userId) {
        return ApiConnection.user(userId);
    }

    @Override
    public Observable<UserRealmModel> userRealmById(@Path("id") int userId) {
        return ApiConnection.userRealm(userId);
    }

    @Override
    public Observable<RealmObject> realmObjectById(@Path("id") int userId) {
        return ApiConnection.realmObject(userId);
    }

    @Override
    public Observable<Object> objectById(@Path("id") int userId) {
        return ApiConnection.objectById(userId);
    }

    @Override
    public Observable<?> userById(@Path("id") int userId) {
        return ApiConnection.userById(userId);
    }

    @Override
    public Observable<Response> getStream(@Path("index") String index) {
        return ApiConnection.getStream(index);
    }
}