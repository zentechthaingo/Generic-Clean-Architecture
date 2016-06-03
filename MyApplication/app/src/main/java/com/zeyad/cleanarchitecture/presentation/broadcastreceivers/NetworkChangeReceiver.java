package com.zeyad.cleanarchitecture.presentation.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.presentation.AndroidApplication;
import com.zeyad.cleanarchitecture.utilities.Utils;

import javax.inject.Inject;

/**
 * @author by ZIaDo on 5/8/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkChangeReceiver.class.getSimpleName();
    @Inject
    RxEventBus rxEventBus;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received notification about network status");
        ((AndroidApplication) context.getApplicationContext()).getApplicationComponent().inject(this);
        isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {
        if (Utils.isNetworkAvailable(context)) {
            rxEventBus.send(100);
            return true;
        }
        return false;
    }
}