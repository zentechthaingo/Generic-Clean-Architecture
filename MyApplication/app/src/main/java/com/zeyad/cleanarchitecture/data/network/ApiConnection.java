package com.zeyad.cleanarchitecture.data.network;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.executor.JobExecutor;
import com.zeyad.cleanarchitecture.utilities.Constants;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;
import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Api Connection class used to retrieve data from the cloud.
 * Implements {@link Callable} so when executed asynchronously can
 * return a value.
 */
public class ApiConnection {

    private static Retrofit retrofit;

    private static Retrofit createRetro2Client() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(new Cache(new File(Constants.CACHE_DIR, "http"), 10485760))
//                .connectionSpecs(Collections.singletonList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                        .tlsVersions(TlsVersion.TLS_1_2)
//                        .cipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
//                        .build()))
                .addNetworkInterceptor(chain -> {
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
                })
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(RestApi.API_BASE_URL)
                .client(okHttpClient)
                .callbackExecutor(new JobExecutor())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setExclusionStrategies(new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes f) {
                                return f.getDeclaringClass().equals(RealmObject.class);
                            }

                            @Override
                            public boolean shouldSkipClass(Class<?> clazz) {
                                return false;
                            }
                        }).create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public static Observable<Collection<UserRealmModel>> userRealmCollection() {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).userRealmModelCollection();
    }

    public static Observable<Collection> userCollection() {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).userCollection();
    }

    public static Observable<UserRealmModel> userRealm(int userId) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).userRealmById(userId);
    }

    public static Observable<Object> objectById(int id) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).objectById(id);
    }

    // TODO: 3/6/16 Test!
    public static Observable<retrofit2.Response> getStream(String userId) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).getStream(userId);
    }

    public static Observable<Object> postItem(Object object) {
        return null;
    }

    public static Observable<Object> deleteCollection(Collection collection) {
        return null;
    }

    public static Observable<Object> deleteItem(Object object) {
        return null;
    }

    public static Observable<Object> deleteItemById(int userId) {
        return null;
    }
}