package com.zeyad.cleanarchitecture.presentation.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;

/**
 * @author by ZIaDo on 5/8/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received notification about network status");
        isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                new RxEventBus().send(100);
                return true;
            }
        }
        return false;
    }
}