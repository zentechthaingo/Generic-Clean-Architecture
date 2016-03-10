package com.zeyad.cleanarchitecturet.presentation.view.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.zeyad.cleanarchitecturet.data.network.RestApi;
import com.zeyad.cleanarchitecturet.presentation.AndroidApplication;
import com.zeyad.cleanarchitecturet.utilities.Constants;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import retrofit.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

// TODO: 3/6/16 Generalize!
public class ImageDownloadIntentService extends IntentService {

    public static final String TAG = ImageDownloadIntentService.class.getSimpleName(),
            EXTRA_PRODUCTS = "products",
            EXTRA_REMOTE_PATH = "REMOTE_PATH",
            EXTRA_REMOTE_NAME = "REMOTE_NAME",
            EXTRA_FILTER_SCHEME = "FILTER",
            EXTRA_CATEGORY = "CATEGORY",
            DOWNLOAD_STATUS_CHANGED = "DOWNLOAD_STATUS_CHANGED",
            EXTENDED_DATA_STATUS = "EXTENDED_DATA_STATUS",
            EXTENDED_DATA_FILE_PATH = "FILE_PATH",
            EXTENDED_DATA_KEY = "KEY",
            EXTENDED_DATA_STATUS_COMPLETED = "COMPLETED",
            EXTENDED_DATA_STATUS_FAILED = "FAILED";
    public static String CACHE_DIR;
    private final Set<String> downloadedKeys = new HashSet<>();
    private final Map<String, List<String>> categorizedKeys = new Hashtable<>();
    // TODO: 1/6/16 Try v
//    private final ArraySet<String> downloadedKeys = new ArraySet<>();
//    private final ArrayMap<String, List<String>> categorizedKeys = new ArrayMap<>();
    private File dir;
    private BitmapFactory.Options bmOptions;
//    @Inject
    RestApi restAdapter;

    public ImageDownloadIntentService() {
        super("ImageDownloadIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        String url = intent.getStringExtra(EXTRA_REMOTE_PATH);
        String name = intent.getStringExtra(EXTRA_REMOTE_NAME) + ".jpg";
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
        File target = new File(dir, name);// check here
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

    private void download(final File target, String index) {
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        restAdapter.getStream(index)
                .groupBy(o -> new AtomicInteger(0).getAndIncrement() % Constants.THREADCT)
                .subscribeOn(Schedulers.from(executor))
//                .observeOn(postExecutionThread.getScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(executor::shutdown
                ).subscribe(new Subscriber<GroupedObservable<Integer, Response>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(GroupedObservable<Integer, Response> response) {
//                try {
//                    InputStream input = response.getBody().in();
//                    int count;
//                    OutputStream output = new FileOutputStream(target);
//                    byte data[] = new byte[1024];
//                    while ((count = input.read(data)) != -1)
//                        output.write(data, 0, count);
//                    output.flush();
//                    output.close();
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
//        restAdapter.create(Controllers.getImages.class).getStream(index, new retrofit.Callback<Response>() {
//            @Override
//            public void success(final Response response, Response response2) {
//                Observable.create(new Observable.OnSubscribe<File>() {
//                    @Override
//                    public void call(Subscriber<? super File> subscriber) {
//                        try {
//                            InputStream input = response.getBody().in();
//                            int count;
//                            OutputStream output = new FileOutputStream(target);
//                            byte data[] = new byte[1024];
//                            while ((count = input.read(data)) != -1)
//                                output.write(data, 0, count);
//                            output.flush();
//                            output.close();
//                            input.close();
//                            subscriber.onNext(target); // Emit the contents of the URL
//                            subscriber.onCompleted(); // Nothing more to emit
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            subscriber.onError(e); // In case there are network errors
//                        }
//                    }
//                }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(new Observer<File>() {
//                    @Override
//                    public void onCompleted() { // Called when the observable has no more data to emit
//                        Log.d("Rx image caching", "Image download complete");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {// Called when the observable encounters an error
//                        Log.d("Rx image caching", "Failed to download image", e);
//                    }
//
//                    @Override
//                    public void onNext(File file) {// Called each time the observable emits data
//                        Log.d("Rx image caching", "Image download successful, name: " + file.getName());
//                    }
//                });
//            }
//
//            @Override
//            public void failure(final RetrofitError error) {
//                Toast.makeText(ImageDownloadIntentService.this, getString(R.string.please_check_internet),
//                        Toast.LENGTH_SHORT).show();
//                Log.e("fail", error.toString());
//            }
//        });
    }
}