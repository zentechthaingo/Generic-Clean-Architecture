package com.zeyad.cleanarchitecture.presentation.services.jobs;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManagerImpl;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.presentation.services.GenericGCMService;
import com.zeyad.cleanarchitecture.presentation.services.GenericJobService;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
// TODO: 5/9/16 switch to DI!

/**
 * @author Zeyad on 6/05/16.
 */
public class Delete {
    public static final String TAG = Delete.class.getSimpleName();
    private Gson gson;
    private Class dataClass;
    private Context mContext;
    private EntityDataMapper entityDataMapper;
    private GeneralRealmManager realmManager;
    private RxEventBus rxEventBus;

    public Delete(Intent intent, RxEventBus rxEventBus, Context context) {
        this.rxEventBus = rxEventBus;
        gson = new Gson();
        mContext = context;
        ArrayList extras = intent.getParcelableArrayListExtra(Constants.EXTRA);
        deleteCollectionFromCloud((List) extras.get(0));
        dataClass = (Class) extras.get(1);
        realmManager = new GeneralRealmManagerImpl(mContext);
        entityDataMapper = new EntityDataMapper();
    }

    public Observable<?> deleteCollectionFromCloud(List list) {
        if (Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
            queueDeleteCollection.call(list);
            return Observable.error(new Exception());
        } else
//            return new RestApiImpl().deleteCollection(list)
//                    .doOnCompleted(() -> {
//                        deleteCollectionGenericsFromCacheAction.call(list);
//                        rxEventBus.send(100);
//                    })
//                    .doOnError(throwable -> queueDeleteCollection.call(list));
            return Observable.just(true);
    }

    private final Func1<List, Boolean> queueDeleteCollection = list -> {
        Bundle extras = new Bundle();
        ArrayList<String> strings = new ArrayList<>();
        extras.putString(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.DELETE_COLLECTION);
        for (Object object : list)
            strings.add(gson.toJson(object));
        extras.putStringArrayList(GenericNetworkQueueIntentService.LIST, strings);
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
            GcmNetworkManager.getInstance(mContext).schedule(new OneoffTask.Builder()
                    .setService(GenericGCMService.class)
                    .setRequiredNetwork(OneoffTask.NETWORK_STATE_ANY)
                    .setRequiresCharging(false)
                    .setExtras(extras)
                    .setPersisted(true)
                    .setUpdateCurrent(false)
                    .setTag(Constants.DELETE_TAG)
                    .build());
            return true;
        } else if (Utils.hasLollipop()) {
            PersistableBundle persistableBundle = new PersistableBundle();
            persistableBundle.putStringArray(GenericNetworkQueueIntentService.LIST, (String[]) strings.toArray());
            return Utils.scheduleJob(mContext, new JobInfo.Builder(1, new ComponentName(mContext, GenericJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setRequiresCharging(false)
                    .setExtras(persistableBundle)
                    .build());
        }
        return false;
    };

    private final Action1<List> deleteCollectionGenericsFromCacheAction = collection -> {
        List<RealmObject> realmObjectList = new ArrayList<>();
        realmObjectList.addAll((List) entityDataMapper.transformAllToRealm(collection, dataClass));
        for (RealmObject realmObject : realmObjectList)
            realmManager.evict(realmObject, dataClass);
    };
}