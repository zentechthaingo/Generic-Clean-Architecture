package com.zeyad.cleanarchitecture.utilities;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.DataBaseManager;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class Utils {

    public static int getNextId(Class clazz, String column) {
        return getMaxId(clazz, column) + 1;
    }

    public static int getMaxId(Class clazz, String column) {
        Number currentMax = Realm.getDefaultInstance().where(clazz).max(column);
        if (currentMax != null)
            return currentMax.intValue();
        else return 0;
    }

    // Simple logging to let us know what each source is returning
    public static Observable.Transformer<List, List> logSources(final String source,
                                                                DataBaseManager realmManager) {
        return observable -> observable.doOnNext(entities -> {
            if (entities == null)
                System.out.println(source + " does not have any data.");
            else if (!realmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
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
                else if (!realmManager.isItemValid(jsonObject.getInt("userId"), "userId", userEntity.getClass()))
                    System.out.println(source + " has stale data.");
                else
                    System.out.println(source + " has the data you are looking for!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null)
            subscription.unsubscribe();
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed())
            return new CompositeSubscription();
        return subscription;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean scheduleJob(Context context, JobInfo jobInfo) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler.schedule(jobInfo) == 1) {
            Log.d("JobScheduler", "Job scheduled successfully!");
            return true;
        } else {
            Log.d("JobScheduler", "Failed to scheduled Job!");
            return false;
        }
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

    // FIXME: 11/05/16 Super Ugly!
    public static EntityMapper getDataMapper(Class dataClass) {
        switch (dataClass.getName()) {
            case "com.zeyad.cleanarchitecture.data.entities.UserRealmModel":
                return new UserEntityDataMapper();
            case "com.zeyad.cleanarchitecture.domain.models.User":
                return new UserEntityDataMapper();
            default:
                return new UserEntityDataMapper();
        }
    }

    public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
        try {
            return clazz.cast(o);
        } catch (ClassCastException e) {
            return null;
        }
    }
}