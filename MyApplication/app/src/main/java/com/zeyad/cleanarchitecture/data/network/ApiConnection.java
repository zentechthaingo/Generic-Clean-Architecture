package com.zeyad.cleanarchitecture.data.network;

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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.realm.RealmObject;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
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

    private static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
//                .addInterceptor(provideDynamicHeaderInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private static OkHttpClient provideOkHttpClient(boolean shouldCache) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
//                .addInterceptor(provideDynamicHeaderInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        if (shouldCache) {
            builder.cache(provideCache());
        }
        return builder.build();
    }

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

//    private static Interceptor provideDynamicHeaderInterceptor() {
//        return chain -> {
//            Request finalRequest = chain.request();
//            if (finalRequest.url().toString().contains("login/storekeeper")
//                    && !TextUtils.isEmpty(Settings.Secure.getString(AndroidApplication.getInstance()
//                    .getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)))
//                finalRequest = chain.request().newBuilder()
//                        .header("uuid", Settings.Secure.getString(AndroidApplication.getInstance()
//                                .getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))
//                        .header("platform", "" + 1)
//                        .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
//                        .method(chain.request().method(), chain.request().body()).build();
//            else if (finalRequest.url().toString().contains("logout/storekeeper"))
//                finalRequest = chain.request().newBuilder()
//                        .header("uuid", Settings.Secure.getString(AndroidApplication.getInstance()
//                                .getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))
//                        .header("platform", "" + 1)
//                        .header("Authorization", "Bearer " + AndroidApplication.getInstance()
//                                .getApplicationContext().getSharedPreferences(Constants.SETTINGS_FILE_NAME,
//                                        Context.MODE_PRIVATE).getString(Constants.ACCESS_TOKEN, ""))
//                        .header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON)
//                        .method(chain.request().method(), chain.request().body()).build();
//            else
//                finalRequest = chain.request().newBuilder()
//                        .header("Authorization", "Bearer " + AndroidApplication.getInstance()
//                                .getApplicationContext().getSharedPreferences(Constants.SETTINGS_FILE_NAME,
//                                        Context.MODE_PRIVATE).getString(Constants.ACCESS_TOKEN, ""))
//                        .method(chain.request().method(), chain.request().body()).build();
//            return chain.proceed(finalRequest);
//        };
//    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message))
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
    }

    public static Interceptor provideCacheInterceptor() {
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

    public static Interceptor provideOfflineCacheInterceptor() {
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

    private static Retrofit createRetro2Client() {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(provideOkHttpClient())
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

    private static Retrofit createRetro2Client(boolean shouldCache) {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(provideOkHttpClient(shouldCache))
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

    public static Observable<ResponseBody> dynamicDownload(String url) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicDownload(url);
    }

    public static Observable<Object> dynamicGetObject(String url) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicGetObject(url);
    }

    public static Observable<Object> dynamicGetObject(String url, boolean shouldCache) {

        Retrofit retrofit = createRetro2Client(shouldCache);
        return retrofit.create(RestApi.class).dynamicGetObject(url);
    }

    public static Observable<List> dynamicGetList(String url) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicGetList(url);
    }

    public static Observable<List> dynamicGetList(String url, boolean shouldCache) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicGetList(url, shouldCache);
    }

    public static Observable<Object> dynamicPostObject(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicPostObject(url, requestBody);
    }

    public static Observable<List> dynamicPostList(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicPostList(url, requestBody);
    }

    public static Observable<Object> dynamicPutObject(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicPutObject(url, requestBody);
    }

    public static Observable<List> dynamicPutList(String url, RequestBody requestBody) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicPutList(url, requestBody);
    }

    // TODO: 13/05/16 Test!
    public static Observable<ResponseBody> upload(String url, MultipartBody.Part file, RequestBody description) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).upload(url, description, file);
    }

    public static Observable<List> dynamicDeleteList(String url, RequestBody body) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicDeleteList(url, body);
    }

    public static Observable<Object> dynamicDeleteObject(String url, RequestBody body) {
        if (retrofit == null)
            retrofit = createRetro2Client();
        return retrofit.create(RestApi.class).dynamicDeleteObject(url, body);
    }
}