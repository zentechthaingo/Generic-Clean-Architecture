package com.zeyad.cleanarchitecture.domain.services;

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
import android.os.RemoteException;
import android.util.Log;

import com.zeyad.cleanarchitecture.presentation.views.activities.BaseActivity;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.LinkedList;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ImageDownloadJobService extends JobService { // runs on the ui thread

    private static final String TAG = ImageDownloadJobService.class.getName();
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
        startService(new Intent(this, ImageDownloadIntentService.class)
                .putExtra(ImageDownloadIntentService.EXTRA_REMOTE_PATH, params.getExtras().getString(""))
                .putExtra(ImageDownloadIntentService.EXTRA_REMOTE_NAME, Utils.getFileNameFromUrl(params
                        .getExtras().getString(""))));
//                .putExtra(ImageDownloadIntentService.WIDTH, getWidth())
//                .putExtra(ImageDownloadIntentService.HEIGHT, getHeight()));
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