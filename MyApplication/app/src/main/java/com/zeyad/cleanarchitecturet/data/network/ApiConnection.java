package com.zeyad.cleanarchitecturet.data.network;

import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.utilities.Constants;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;

/**
 * Api Connection class used to retrieve data from the cloud.
 * Implements {@link Callable} so when executed asynchronously can
 * return a value.
 */
public class ApiConnection {

    private static Retrofit retrofit;

    private static Retrofit createRetro2Client() {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setCache(new Cache(new File(Constants.CACHE_DIR, "http"), 10485760));
        okHttpClient.interceptors().add(chain -> {
            Request request = chain.request();
            Log.d("OkHttp REQUEST", request.toString());
            Log.d("OkHttp REQUEST Headers", request.headers().toString());
            Response response = chain.proceed(request);
            response = response.newBuilder()
                    .header("Cache-Control", String.format("public, max-age=%d, max-stale=%d",
                            60, 2419200)).build();
            Log.d("OkHttp RESPONSE", response.toString());
            Log.d("OkHttp RESPONSE Headers", response.headers().toString());
            return response;
        });
        return new Retrofit.Builder()
                .baseUrl(RestApi.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static Observable<List<UserEntity>> userList() {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).userEntityList();
    }

    public static Observable<UserEntity> user(int userId) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).userEntityById(userId);
    }

    // TODO: 3/6/16 Test!
    public static Observable<retrofit.Response> getStream(String userId) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).getStream(userId);
    }
}