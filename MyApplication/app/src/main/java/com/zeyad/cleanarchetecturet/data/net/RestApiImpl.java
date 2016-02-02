package com.zeyad.cleanarchetecturet.data.net;

import com.zeyad.cleanarchetecturet.data.entity.ProductEntity;
import com.zeyad.cleanarchetecturet.data.entity.UserEntity;

import java.util.List;

import retrofit.http.Path;
import rx.Observable;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

    public RestApiImpl() {
    }

    @Override
    public Observable<List<UserEntity>> userEntityList() {
        return ApiConnection.userList();
    }

    @Override
    public Observable<UserEntity> userEntityById(final int userId) {
        return ApiConnection.user(userId);
    }

    // TODO: 2/2/16 Implement!
    @Override
    public Observable<List<ProductEntity>> getProductList() {
        return null;
    }

    @Override
    public Observable<ProductEntity> getProductById(final int id) {
        return null;
    }
}