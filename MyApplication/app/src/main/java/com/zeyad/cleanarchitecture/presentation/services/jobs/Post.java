package com.zeyad.cleanarchitecture.presentation.services.jobs;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManagerImpl;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.presentation.services.GenericGCMService;
import com.zeyad.cleanarchitecture.presentation.services.GenericJobService;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;

import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
// TODO: 5/9/16 switch to DI!
/**
 * @author Zeyad on 6/05/16.
 */
public class Post {
    public static final String TAG = Post.class.getSimpleName();
    private Gson gson;
    private Class dataClass;
    private Context mContext;
    private UserEntityDataMapper entityDataMapper;
    private GeneralRealmManager realmManager;
    private RxEventBus rxEventBus;

    public Post(Intent intent, RxEventBus rxEventBus, Context context) {
        gson = new Gson();
        this.rxEventBus = rxEventBus;
        mContext = context;
        ArrayList extras = intent.getParcelableExtra(Constants.EXTRA);
        postToCloud(extras.get(0));
        dataClass = (Class) extras.get(1);
        realmManager = new GeneralRealmManagerImpl(mContext);
        entityDataMapper = new UserEntityDataMapper();
    }

    public Observable<?> postToCloud(Object object) {
        if (Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
            queuePost.call(object);
            return Observable.error(new Exception());
        } else {
//            return new RestApiImpl().postItem(object)
//                    .doOnNext(o -> {
//                        saveGenericToCacheAction.call(o);
//                        rxEventBus.send(new Pair<>(200, o));
//                    })
//                    .doOnError(throwable -> queuePost.call(object));
            return Observable.just(true);
        }
    }

    private final Action1<Object> saveGenericToCacheAction =
            object -> realmManager.put((RealmObject) entityDataMapper.transformToRealm(object, dataClass))
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DefaultSubscriber<Object>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Object o) {
                            Log.d(TAG, object.getClass().getName() + " added!");
                        }
                    });

    private Func1<Object, Boolean> queuePost = object -> {
        Bundle extras = new Bundle();
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.POST);
        extras.putString(GenericNetworkQueueIntentService.POST_OBJECT, new Gson().toJson(object));
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
            GcmNetworkManager.getInstance(mContext).schedule(new OneoffTask.Builder()
                    .setService(GenericGCMService.class)
                    .setRequiredNetwork(OneoffTask.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .setUpdateCurrent(false)
                    .setPersisted(true)
                    .setExtras(extras)
                    .setTag(Constants.POST_TAG)
                    .build());
            return true;
        } else if (Utils.hasLollipop()) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putString(GenericNetworkQueueIntentService.POST_OBJECT, new Gson().toJson(object));
            return Utils.scheduleJob(mContext, new JobInfo.Builder(1, new ComponentName(mContext, GenericJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setExtras(persistableBundle)
                    .build());
        }
        return false;
    };
}