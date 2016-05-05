package com.zeyad.cleanarchitecture.presentation.services;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.zeyad.cleanarchitecture.utilities.Utils;

public class GenericGCMService extends GcmTaskService {

    public static final String TAG = GenericNetworkQueueIntentService.class.getSimpleName(),
            EXTRA_REMOTE_PATH = "REMOTE_PATH",
            TAG_TASK_ONE_OFF_LOG = "one_off_task",
            TAG_TASK_PERIODIC_LOG = "periodic_task";

    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();
        // Reschedule removed tasks here
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        switch (taskParams.getTag()) {
            case TAG_TASK_ONE_OFF_LOG:
                Log.i(TAG, TAG_TASK_ONE_OFF_LOG);
                String url = taskParams.getExtras().getString(EXTRA_REMOTE_PATH);
                startService(new Intent(getApplicationContext(), GenericNetworkQueueIntentService.class)
                        .putExtra(GenericNetworkQueueIntentService.EXTRA_REMOTE_PATH, url)
                        .putExtra(GenericNetworkQueueIntentService.EXTRA_REMOTE_NAME,
                                Utils.getFileNameFromUrl(url)));
                return GcmNetworkManager.RESULT_SUCCESS;
            case TAG_TASK_PERIODIC_LOG:
                Log.i(TAG, TAG_TASK_PERIODIC_LOG);
                // This is where useful work would go
                return GcmNetworkManager.RESULT_SUCCESS;
            default:
                return GcmNetworkManager.RESULT_FAILURE;
        }
    }
}