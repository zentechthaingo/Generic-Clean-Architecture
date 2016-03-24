package com.zeyad.cleanarchitecturet.utilities;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecturet.data.db.RealmManager;
import com.zeyad.cleanarchitecturet.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecturet.data.entities.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class Utils {

    private static final int COUNTER_START = 1, ATTEMPTS = 5;

    public static <T> Observable.Transformer<T, Long> zipWithFlatMap(String TAG) {
        return observable -> observable.zipWith(Observable.range(COUNTER_START, ATTEMPTS), (t, repeatAttempt) -> {
            Log.v(TAG, "zipWith, call, repeatAttempt " + repeatAttempt);
            return repeatAttempt;
        }).flatMap(repeatAttempt -> {
            Log.v(TAG, "flatMap, call, repeatAttempt " + repeatAttempt);
            // increase the waiting time
            return Observable.timer(repeatAttempt * 5, TimeUnit.SECONDS);
        });
    }

    // Simple logging to let us know what each source is returning
    public static Observable.Transformer<Collection<UserEntity>, Collection<UserEntity>>
    logUsersSources(final String source, RealmManager realmManager) {
        return observable -> observable.doOnNext(userEntities -> {
            if (userEntities == null)
                System.out.println(source + " does not have any data.");
            else if (!realmManager.areUsersValid())
                System.out.println(source + " has stale data.");
            else
                System.out.println(source + " has the data you are looking for!");
        });
    }

    // Simple logging to let us know what each source is returning
    public static Observable.Transformer<Collection, Collection> logSources(final String source,
                                                                            GeneralRealmManager realmManager) {
        return observable -> observable.doOnNext(entities -> {
            if (entities == null)
                System.out.println(source + " does not have any data.");
            else if (!realmManager.areItemsValid(entities.getClass()))
                System.out.println(source + " has stale data.");
            else
                System.out.println(source + " has the data you are looking for!");
        });
    }

    // Simple logging to let us know what each source is returning
    public static Observable.Transformer<UserEntity, UserEntity> logUserSource(final String source,
                                                                               RealmManager realmManager) {
        return observable -> observable.doOnNext(userEntity -> {
            if (userEntity == null)
                System.out.println(source + " does not have any data.");
            else if (!realmManager.isUserValid(userEntity.getUserId()))
                System.out.println(source + " has stale data.");
            else
                System.out.println(source + " has the data you are looking for!");
        });
    }

    // Fixme: 3/22/16 fix!
    // Simple logging to let us know what each source is returning
    public static <T> Observable.Transformer<T, T> logSource(final String source,
                                                             GeneralRealmManager realmManager) {
        return observable -> observable.doOnNext(userEntity -> {
            try {
                JSONObject jsonObject = new JSONObject(new Gson().toJson(userEntity));
                if (userEntity == null)
                    System.out.println(source + " does not have any data.");
                else if (!realmManager.isItemValid(jsonObject.getInt("userId"), userEntity.getClass()))
                    System.out.println(source + " has stale data.");
                else
                    System.out.println(source + " has the data you are looking for!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    // TODO: 1/5/16 Test!
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context, JobInfo jobInfo) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler.schedule(jobInfo) == 1)
            Log.d("JobScheduler", "Job scheduled successfully!");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }
        return false;
    }

    /**
     * Creates a file name from an image url
     *
     * @param imageUrl The image url used to build the file name.
     * @return An String representing a unique file name.
     */
    public static String getFileNameFromUrl(String imageUrl) {
        //we could generate an unique MD5/SHA-1 here
        String hash = String.valueOf(imageUrl.hashCode());
        if (hash.startsWith("-"))
            hash = hash.substring(1);
        return Constants.BASE_IMAGE_NAME_CACHED + hash + Constants.IMAGE_EXTENSION;
    }

    /**
     * Creates a file name from an image url
     *
     * @param fileName The image url used to build the file name.
     * @return A {@link File} representing a unique element.
     */
    public static File buildFileFromFilename(String fileName) {
        return new File(Constants.CACHE_DIR + File.separator + fileName);
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}