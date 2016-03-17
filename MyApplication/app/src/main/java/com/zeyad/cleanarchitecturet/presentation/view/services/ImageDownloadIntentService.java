package com.zeyad.cleanarchitecturet.presentation.view.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.zeyad.cleanarchitecturet.data.network.RestApi;
import com.zeyad.cleanarchitecturet.data.network.RestApiImpl;
import com.zeyad.cleanarchitecturet.utilities.Constants;
import com.zeyad.cleanarchitecturet.utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

// TODO: 3/6/16 Generalize!
public class ImageDownloadIntentService extends IntentService {
    //    @Inject
    private RestApi restAdapter;
    public static final String TAG = ImageDownloadIntentService.class.getSimpleName(),
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
            WIDTH = "WIDTH",
            HEIGHT = "HEIGHT";
    public static String CACHE_DIR;
    private final Set<String> downloadedKeys = new HashSet<>();
    private final Map<String, List<String>> categorizedKeys = new Hashtable<>();
    private File dir;
    private BitmapFactory.Options bmOptions;
    private Intent intent;

    public ImageDownloadIntentService() {
        super("ImageDownloadIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (restAdapter == null)
            restAdapter = new RestApiImpl();
        CACHE_DIR = Constants.CACHE_DIR = new File(String.valueOf(getCacheDir())).getAbsolutePath();
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
        bmOptions = new BitmapFactory.Options();
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        addAllCachedFiles();
//        AndroidApplication.getInstance().getApplicationComponent().inject(this);
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.intent = intent;
        String url = intent.getStringExtra(EXTRA_REMOTE_PATH);
        String name = Utils.getFileNameFromUrl(url);
        String cat = intent.getStringExtra(EXTRA_CATEGORY);
        String filter = intent.getStringExtra(EXTRA_FILTER_SCHEME);
        boolean localCopy = false;
        if (url.startsWith(Constants.CACHE_DIR))
            if (new File(url).exists()) {
                url = url.replaceFirst(Constants.CACHE_DIR, "");
                downloadedKeys.add(url);
                localCopy = true;
            }
        Intent localIntent = new Intent(DOWNLOAD_STATUS_CHANGED + "-" + filter).putExtra(EXTENDED_DATA_KEY,
                url);
        File target = new File(dir, name);
        String targetPath = target.getAbsolutePath();
        if (downloadedKeys.contains(url)) {
            if (localCopy) {
                targetPath = Constants.CACHE_DIR + url;
                Log.d(TAG, "Found in wallet " + url + ", file " + targetPath);
            } else {
                Log.d(TAG, "Found in cache " + url + ", file " + targetPath);
            }
            localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_COMPLETED)
                    .putExtra(EXTENDED_DATA_FILE_PATH, targetPath);
        } else {
            if (target.exists()) {
                downloadedKeys.add(url);
            } else {
                Log.d(TAG, "Downloading " + url + " into " + targetPath);
                try {
                    download(target, url.substring(url.lastIndexOf("/") + 1, url.length() - 4));
//                    download(target, url);
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
                    if (target.exists()) {
                        target.delete();
                        Log.e(TAG, "Delete corrupted file");
                    }
                    e.printStackTrace();
                    localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_FAILED);
                }
            }
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    // FIXME: 3/17/16 fix!
    private void download(final File target, String imageUrl) {
        try {
            FileOutputStream fOut = new FileOutputStream(target);
            Glide.with(getApplicationContext())
                    .load(imageUrl)
                    .asBitmap()
                    .into(intent.getIntExtra(WIDTH, 100), intent.getIntExtra(HEIGHT, 100))
                    .get()
                    .compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }
}