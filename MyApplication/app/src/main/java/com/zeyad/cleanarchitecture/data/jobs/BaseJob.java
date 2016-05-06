package com.zeyad.cleanarchitecture.data.jobs;

import android.os.Bundle;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

/**
 * @author Zeyad on 6/05/16.
 */
public class BaseJob extends Job {
    Bundle mExtras;

    protected BaseJob(Params params) {
        super(params);
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
    }

    @Override
    protected void onCancel(int cancelReason) {
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}