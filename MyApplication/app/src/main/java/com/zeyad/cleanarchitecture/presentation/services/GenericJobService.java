package com.zeyad.cleanarchitecture.presentation.services;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.zeyad.cleanarchitecture.presentation.screens.BaseActivity;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.LinkedList;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class GenericJobService extends JobService { // runs on the ui thread

    private static final String TAG = GenericJobService.class.getName();
    private BaseActivity mActivity;
    private final LinkedList<JobParameters> jobParamsMap = new LinkedList<>();

    public void setUiCallback(BaseActivity activity) {
        mActivity = activity;
    }

    /**
     * Send job to the JobScheduler.
     */
    public void scheduleJob(JobInfo t) {
        Log.d(TAG, "Scheduling job");
        JobScheduler tm = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.schedule(t);
    }

    /**
     * Not currently used, but as an exercise you can hook this
     * up to a button in the UI to finish a job that has landed
     * in onStartJob().
     */
    public boolean callJobFinished() {
        JobParameters params = jobParamsMap.poll();
        if (params == null) {
            return false;
        } else {
            jobFinished(params, false);
            return true;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCalback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Messenger callback = intent.getParcelableExtra("messenger");
        Message m = Message.obtain();
//        m.what = BaseActivity.MSG_SERVICE_OBJ;
        m.obj = this;
        try {
            callback.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object back to activity.");
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) { // return true if u r doing background thread work, else return false
        switch (params.getExtras().getString(GenericNetworkQueueIntentService.JOB_TYPE, "")) {
            case GenericNetworkQueueIntentService.DOWNLOAD_IMAGE:
                String url = params.getExtras().getString(GenericNetworkQueueIntentService.EXTRA_REMOTE_PATH);
                startService(new Intent(getApplicationContext(), GenericNetworkQueueIntentService.class)
                        .putExtra(GenericNetworkQueueIntentService.EXTRA_REMOTE_PATH, url)
                        .putExtra(GenericNetworkQueueIntentService.EXTRA_REMOTE_NAME,
                                Utils.getFileNameFromUrl(url))
                        .putExtra(GenericNetworkQueueIntentService.WIDTH, -1)
                        .putExtra(GenericNetworkQueueIntentService.HEIGHT, -1));
                Log.d(TAG, "DownloadImage Job started!");
                break;
            case GenericNetworkQueueIntentService.UPLOAD_IMAGE:
                // not yet
                break;
            case GenericNetworkQueueIntentService.POST_OBJECT:
                startService(new Intent(this, GenericNetworkQueueIntentService.class)
                        .putExtra(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.POST)
                        .putExtra(GenericNetworkQueueIntentService.POST_OBJECT,
                                (Parcelable) params.getExtras()
                                        .get(GenericNetworkQueueIntentService.POST_OBJECT)));
                Log.d(TAG, "Post Object Job Started!");
                break;
            case GenericNetworkQueueIntentService.DELETE_COLLECTION:
                startService(new Intent(this, GenericNetworkQueueIntentService.class)
                        .putExtra(GenericNetworkQueueIntentService.JOB_TYPE, GenericNetworkQueueIntentService.DELETE_COLLECTION)
                        .putExtra(GenericNetworkQueueIntentService.DELETE_COLLECTION,
                                (Parcelable) params.getExtras()
                                        .get(GenericNetworkQueueIntentService.DELETE_COLLECTION)));
                Log.d(TAG, "Delete Collection Job Started!");
                break;
            default:
                break;
        }
        jobFinished(params, false);// true to reschedule, false to drop
        return true;
    }

    // called if the preset conditions changed during the job is running.
    @Override
    public boolean onStopJob(JobParameters params) { // return true if u want to reschedule, false to drop
        // clean up if u need
        return false;
    }
}