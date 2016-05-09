package com.zeyad.cleanarchitecture.presentation.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.presentation.AndroidApplication;
import com.zeyad.cleanarchitecture.presentation.services.jobs.Delete;
import com.zeyad.cleanarchitecture.presentation.services.jobs.DownloadImage;
import com.zeyad.cleanarchitecture.presentation.services.jobs.Post;
import com.zeyad.cleanarchitecture.presentation.services.jobs.UploadImage;

import javax.inject.Inject;

public class GenericNetworkQueueIntentService extends IntentService {

    public static final String TAG = GenericNetworkQueueIntentService.class.getSimpleName(),
            EXTRA_REMOTE_PATH = "REMOTE_PATH",
            EXTRA_REMOTE_NAME = "REMOTE_NAME",
            WIDTH = "WIDTH", HEIGHT = "HEIGHT",
            POST_OBJECT = "POST_OBJECT",
            DELETE_COLLECTION = "DELETE_COLLECTION",
            DOWNLOAD_IMAGE = "DOWNLOAD_IMAGE",
            UPLOAD_IMAGE = "UPLOAD_IMAGE",
            JOB_TYPE = "JOB_TYPE",
            POST = "POST", LIST = "LIST";
    @Inject
    RxEventBus rxEventBus;

    public GenericNetworkQueueIntentService() {
        super(GenericNetworkQueueIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((AndroidApplication) getApplicationContext()).getApplicationComponent().inject(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getStringExtra(JOB_TYPE)) {
            case DOWNLOAD_IMAGE:
                new DownloadImage(intent, rxEventBus, this);
                break;
            case UPLOAD_IMAGE:
                new UploadImage(intent, rxEventBus);
                break;
            case POST_OBJECT:
                new Post(intent, rxEventBus, this);
                break;
            case DELETE_COLLECTION:
                new Delete(intent, rxEventBus, this);
                break;
            default:
                break;
        }
    }
}