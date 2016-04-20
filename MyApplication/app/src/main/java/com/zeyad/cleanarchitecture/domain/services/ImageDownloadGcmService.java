package com.zeyad.cleanarchitecture.domain.services;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.zeyad.cleanarchitecture.data.network.RestApiImpl;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import rx.Subscriber;

// TODO: 4/19/16 Inject data layer
public class ImageDownloadGcmService extends GcmTaskService {

    public static final String TAG = ImageDownloadIntentService.class.getSimpleName(),
            EXTRA_REMOTE_PATH = "REMOTE_PATH",
            EXTRA_FILTER_SCHEME = "FILTER",
            EXTRA_CATEGORY = "CATEGORY",
            DOWNLOAD_STATUS_CHANGED = "DOWNLOAD_STATUS_CHANGED",
            EXTENDED_DATA_STATUS = "EXTENDED_DATA_STATUS",
            EXTENDED_DATA_FILE_PATH = "FILE_PATH",
            EXTENDED_DATA_KEY = "KEY",
            EXTENDED_DATA_STATUS_COMPLETED = "COMPLETED",
            EXTENDED_DATA_STATUS_FAILED = "FAILED",
            TAG_TASK_ONE_OFF_LOG = "one_off_task",
            TAG_TASK_PERIODIC_LOG = "periodic_task";
    public static String CACHE_DIR;
    private final Set<String> downloadedKeys = new HashSet<>();
    private final Map<String, List<String>> categorizedKeys = new Hashtable<>();
    // TODO: 1/6/16 Try v
//    private final ArraySet<String> downloadedKeys = new ArraySet<>();
//    private final ArrayMap<String, List<String>> categorizedKeys = new ArrayMap<>();
    private File dir;
    private BitmapFactory.Options bmOptions;
    private final RestApiImpl restApi = new RestApiImpl();

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
                // This is where useful work would go
                Constants.CACHE_DIR = new File(String.valueOf(getCacheDir())).getAbsolutePath();
                CACHE_DIR = Constants.CACHE_DIR;
                dir = new File(CACHE_DIR);
                File lockSignature = new File(dir, "dl.lock");
                if (!dir.exists()) {
                    dir.mkdirs();
                } else {
                    if (lockSignature.exists()) {
                        // TODO: check signature
                    } else {
                        // TODO: generate signature
                    }
                }
                addAllCachedFiles();
                String url = taskParams.getExtras().getString(EXTRA_REMOTE_PATH);
                String name = Utils.getFileNameFromUrl(url);
                String cat = taskParams.getExtras().getString(EXTRA_CATEGORY);
                String filter = taskParams.getExtras().getString(EXTRA_FILTER_SCHEME);
                boolean localCopy = false;
                if (name.startsWith(Constants.CACHE_DIR) && Utils.buildFileFromFilename(Utils
                        .getFileNameFromUrl(url)).exists()) {
                    downloadedKeys.add(url);
                    localCopy = true;
                }
                Intent localIntent = new Intent(DOWNLOAD_STATUS_CHANGED + "-" + filter)
                        .putExtra(EXTENDED_DATA_KEY, url);
                File target = Utils.buildFileFromFilename(name);
                String targetPath = target.getAbsolutePath();
                if (downloadedKeys.contains(url)) {
                    if (localCopy)
                        Log.d(TAG, "Found in wallet " + url + ", file " + targetPath);
                    else
                        Log.d(TAG, "Found in cache " + url + ", file " + targetPath);
                    localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_COMPLETED)
                            .putExtra(EXTENDED_DATA_FILE_PATH, targetPath);
                } else {
                    if (target.exists()) {
                        downloadedKeys.add(url);
                    } else {
                        Log.d(TAG, "Downloading " + url + " into " + targetPath);
                        try {
                            download(target, Integer.parseInt(url.charAt(url.lastIndexOf("_") + 1) + ""));
                            downloadedKeys.add(url);
                            if (cat != null && !cat.isEmpty()) {
                                if (!categorizedKeys.containsKey(cat))
                                    categorizedKeys.put(cat, new ArrayList<String>());
                                categorizedKeys.get(cat).add(url);
                            }
                            localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_COMPLETED)
                                    .putExtra(EXTENDED_DATA_FILE_PATH, targetPath);
                        } catch (Exception e) {
                            target = new File(targetPath);
                            if (target.exists())
                                Log.e(TAG, "Delete corrupted file: " + target.delete());
                            e.printStackTrace();
                            localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_FAILED);
                        }
                    }
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
                return GcmNetworkManager.RESULT_SUCCESS;
            case TAG_TASK_PERIODIC_LOG:
                Log.i(TAG, TAG_TASK_PERIODIC_LOG);
                // This is where useful work would go
                return GcmNetworkManager.RESULT_SUCCESS;
            default:
                return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    private void addAllCachedFiles() {
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files) {
                String key = file.getAbsolutePath().replace(CACHE_DIR, "");
                if (!downloadedKeys.contains(key)) {
                    Log.d(TAG, "Preloaded cached file: " + key);
                    downloadedKeys.add(key);
                }
            }
    }

    private void download(final File target, int index) {
        restApi.download(index).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    try {
                        byte[] fileReader = new byte[4096];
                        long fileSize = responseBody.contentLength();
                        long fileSizeDownloaded = 0;
                        inputStream = responseBody.byteStream();
                        outputStream = new FileOutputStream(target);
                        while (true) {
                            int read = inputStream.read(fileReader);
                            if (read == -1)
                                break;
                            outputStream.write(fileReader, 0, read);
                            fileSizeDownloaded += read;
                            Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                        }
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null)
                            inputStream.close();
                        if (outputStream != null)
                            outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}