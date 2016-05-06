package com.zeyad.cleanarchitecture.data.jobs;

import android.content.Intent;
import android.os.Bundle;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;

/**
 * @author Zeyad on 6/05/16.
 */
public class NetworkJob extends BaseJob {

    public NetworkJob(Params params, Bundle extras) {
        super(params);
        mExtras = extras;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        getApplicationContext().startService(new Intent(getApplicationContext(), GenericNetworkQueueIntentService.class)
                .putExtra(GenericNetworkQueueIntentService.EXTRA_BUNDLE, mExtras));
    }

    @Override
    protected void onCancel(int cancelReason) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}