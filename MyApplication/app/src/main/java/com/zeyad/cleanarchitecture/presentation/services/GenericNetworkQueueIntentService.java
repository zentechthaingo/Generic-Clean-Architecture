package com.zeyad.cleanarchitecture.presentation.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.zeyad.cleanarchitecture.data.network.RestApiImpl;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.presentation.AndroidApplication;
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
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;

// TODO: 5/4/16 Generalize!
public class GenericNetworkQueueIntentService extends IntentService {

    public static final String TAG = GenericNetworkQueueIntentService.class.getSimpleName(),
            EXTRA_REMOTE_PATH = "REMOTE_PATH",
            EXTRA_REMOTE_NAME = "REMOTE_NAME",
            EXTRA_FILTER_SCHEME = "FILTER",
            EXTRA_CATEGORY = "CATEGORY",
            DOWNLOAD_STATUS_CHANGED = "DOWNLOAD_STATUS_CHANGED",
            EXTENDED_DATA_STATUS = "EXTENDED_DATA_STATUS",
            EXTENDED_DATA_FILE_PATH = "FILE_PATH",
            EXTENDED_DATA_KEY = "KEY",
            EXTENDED_DATA_STATUS_COMPLETED = "COMPLETED",
            EXTENDED_DATA_STATUS_FAILED = "FAILED",
            WIDTH = "WIDTH", HEIGHT = "HEIGHT",
            POST_OBJECT = "POST_OBJECT",
            DELETE_COLLECTION = "DELETE_COLLECTION";
    private final Set<String> downloadedKeys = new HashSet<>();
    private final Map<String, List<String>> categorizedKeys = new Hashtable<>();
    private File dir;
    @Inject
    RxEventBus rxEventBus;

    public GenericNetworkQueueIntentService() {
        super("GenericNetworkQueueIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((AndroidApplication) getApplicationContext()).getApplicationComponent().inject(this);
        Constants.CACHE_DIR = new File(String.valueOf(getCacheDir())).getAbsolutePath();
        dir = new File(Constants.CACHE_DIR);
        File lockSignature = new File(dir, "dl.lock");
        if (!dir.exists()) {
            dir.mkdirs();
        } else if (lockSignature.exists()) {
            // TODO: check signature
        } else {
            // TODO: generate signature
        }
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        addAllCachedFiles();
    }

    private void addAllCachedFiles() {
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files) {
                String key = file.getAbsolutePath().replace(Constants.CACHE_DIR, "");
                if (!downloadedKeys.contains(key)) {
                    Log.d(TAG, "Preloaded cached file: " + key);
                    downloadedKeys.add(key);
                }
            }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(EXTRA_REMOTE_PATH);
        String name = Utils.getFileNameFromUrl(url);
        String cat = intent.getStringExtra(EXTRA_CATEGORY);
        String filter = intent.getStringExtra(EXTRA_FILTER_SCHEME);
        boolean localCopy = false;
        if (name.startsWith(Constants.CACHE_DIR))
            if (Utils.buildFileFromFilename(Utils.getFileNameFromUrl(url)).exists()) {
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
    }

    private void download(final File target, int index) {
        // TODO: 5/4/16 Test!
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(target);
            Glide.with(GenericNetworkQueueIntentService.this)
                    .load(Constants.BASE_URL + "cover_" + index + ".jpg")
                    .asBitmap()
                    .into(-1, -1)
                    .get()
                    .compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            rxEventBus.send("file Downloaded!");
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        RestApiImpl restApi = new RestApiImpl();
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
                        rxEventBus.send("file Downloaded!");
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