package com.zeyad.cleanarchitecture.data.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.zeyad.cleanarchitecture.BuildConfig;
import com.zeyad.cleanarchitecture.data.executor.JobExecutor;
import com.zeyad.cleanarchitecture.presentation.AndroidApplication;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
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
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final int TIME_OUT = 30;

    @Nullable
    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(AndroidApplication.getInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    public static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message))
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());
            // re-write response header to force use of cache
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(2, TimeUnit.MINUTES)
                    .build();
            return response.newBuilder()
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();
            if (!Utils.isNetworkAvailable(AndroidApplication.getInstance().getApplicationContext())) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(1, TimeUnit.DAYS)
                        .build();
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            return chain.proceed(request);
        };
    }

    public static OkHttpClient provideOkHttpClient(boolean shouldCache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .authenticator(provideAuthenticator())
//                .addInterceptor(provideExpiredAccessTokenInterceptor())
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
//                .addInterceptor(provideDynamicHeaderInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        if (shouldCache)
            builder.cache(provideCache());
        return builder.build();
    }

    @NonNull
    private static RequestBody forceContentLength(@NonNull final RequestBody requestBody) throws IOException {
        final Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                sink.write(buffer.snapshot());
            }
        };
    }

    @NonNull
    private static RequestBody gzip(@NonNull final RequestBody body) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return -1; // We don't know the compressed length in advance!
            }

            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }

    private static Retrofit createRetro2Client(boolean shouldCache) {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(provideOkHttpClient(shouldCache))
                .callbackExecutor(new JobExecutor())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setExclusionStrategies(new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(@NonNull FieldAttributes f) {
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

    public static Observable<ResponseBody> dynamicDownload(String url) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicDownload(url);
    }

    public static Observable<Object> dynamicGetObject(String url) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicGetObject(url);
    }

    public static Observable<Object> dynamicGetObject(String url, boolean shouldCache) {
        Retrofit retrofit = createRetro2Client(shouldCache);
        return retrofit.create(RestApi.class).dynamicGetObject(url);
    }

    public static Observable<List> dynamicGetList(String url) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicGetList(url);
    }

    public static Observable<List> dynamicGetList(String url, boolean shouldCache) {
        if (retrofit == null)
            retrofit = createRetro2Client(shouldCache);
        return retrofit.create(RestApi.class).dynamicGetList(url);
    }

    public static Observable<Object> dynamicPostObject(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicPostObject(url, requestBody);
    }

    public static Observable<List> dynamicPostList(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicPostList(url, requestBody);
    }

    public static Observable<Object> dynamicPutObject(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicPutObject(url, requestBody);
    }

    public static Observable<List> dynamicPutList(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicPutList(url, requestBody);
    }

    public static Observable<ResponseBody> upload(String url, MultipartBody.Part file, RequestBody description) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).upload(url, description, file);
    }

    public static Observable<Object> upload(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).upload(url, requestBody);
    }

    public static Observable<ResponseBody> upload(String url, MultipartBody.Part file) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).upload(url, file);
    }

    public static Observable<List> dynamicDeleteList(String url, RequestBody body) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicDeleteList(url, body);
    }

    public static Observable<Object> dynamicDeleteObject(String url, RequestBody body) {
        if (retrofit == null)
            retrofit = createRetro2Client(false);
        return retrofit.create(RestApi.class).dynamicDeleteObject(url, body);
    }

//    public static Call<RefreshTokenEntity> refreshToken(String url, RequestBody body) {
//        if (retrofit == null)
//            retrofit = createRetro2Client(false);
//        return retrofit.create(RestApi.class).refreshToken(url, body);
//    }
}