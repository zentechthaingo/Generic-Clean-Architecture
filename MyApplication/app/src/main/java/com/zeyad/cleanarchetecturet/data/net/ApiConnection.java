package com.zeyad.cleanarchetecturet.data.net;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zeyad.cleanarchetecturet.data.entity.UserEntity;
import com.zeyad.cleanarchetecturet.utilities.Constants;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;
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
        okHttpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
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
            }
        });
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(RealmObject.class);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        return new Retrofit.Builder()
                .baseUrl(RestApi.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(WireConverterFactory.create())
//                .addConverterFactory(ProtoConverterFactory.create())
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
}