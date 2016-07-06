package com.zeyad.cleanarchitecture.presentation.services.jobs;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManagerImpl;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action1;

/**
 * @author Zeyad on 6/05/16.
 */
// TODO: 5/9/16 switch to DI!
public class Delete {
    public static final String TAG = Delete.class.getSimpleName();
    private Gson gson;
    private Class dataClass;
    private Context mContext;
    private UserEntityDataMapper entityDataMapper;
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
        entityDataMapper = new UserEntityDataMapper();
    }

    public Observable<?> deleteCollectionFromCloud(List list) {
        if (Utils.isNetworkAvailable(mContext) && (Utils.hasLollipop()
                || GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS)) {
//            CloudDataStore.queueDeleteCollection.call(list);
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

    private final Action1<List> deleteCollectionGenericsFromCacheAction = collection -> {
        List<RealmObject> realmObjectList = new ArrayList<>();
        realmObjectList.addAll((List) entityDataMapper.transformAllToRealm(collection, dataClass));
        for (RealmObject realmObject : realmObjectList)
            realmManager.evict(realmObject, dataClass);
    };
}