package com.zeyad.cleanarchitecture.presentation.services.jobs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.zeyad.cleanarchitecture.data.network.RestApiImpl;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Zeyad on 6/05/16.
 */
public class DownloadImage {

    public static final String TAG = DownloadImage.class.getSimpleName();
    private final Set<String> downloadedKeys = new HashSet<>();
    private File dir;
    RxEventBus rxEventBus;

    public DownloadImage(Intent intent, RxEventBus rxEventBus, Context context) {
        this.rxEventBus = rxEventBus;
        if (TextUtils.isEmpty(Constants.CACHE_DIR))
            Constants.CACHE_DIR = new File(String.valueOf(context.getCacheDir())).getAbsolutePath();
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
        onHandleIntent(intent);
    }

    private void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(GenericNetworkQueueIntentService.EXTRA_REMOTE_PATH);
        String name = Utils.getFileNameFromUrl(url);
        int width = intent.getIntExtra(GenericNetworkQueueIntentService.WIDTH, 100);
        int height = intent.getIntExtra(GenericNetworkQueueIntentService.HEIGHT, 100);
        if (Utils.buildFileFromFilename(Utils.getFileNameFromUrl(url)).exists())
            downloadedKeys.add(url);
        File target = Utils.buildFileFromFilename(name);
        String targetPath = target.getAbsolutePath();
        if (!downloadedKeys.contains(url) && target.exists())
            downloadedKeys.add(url);
        else {
            Log.d(TAG, "Downloading " + url + " into " + targetPath);
            try {
//                download(target, Integer.parseInt(url.charAt(url.lastIndexOf("_") + 1) + ""), width,
//                        height);
                retroDownload(target, Integer.parseInt(url.charAt(url.lastIndexOf("_") + 1) + ""));
                downloadedKeys.add(url);
            } catch (Exception e) {
                target = new File(targetPath);
                if (target.exists())
                    Log.e(TAG, "Delete corrupted file: " + target.delete());
                e.printStackTrace();
            }
        }
    }

    private void addAllCachedFiles() {
        File[] files = dir.listFiles();
        String key;
        if (files != null)
            for (File file : files) {
                key = file.getAbsolutePath().replace(Constants.CACHE_DIR, "");
                if (!downloadedKeys.contains(key)) {
                    Log.d(TAG, "Preloaded cached file: " + key);
                    downloadedKeys.add(key);
                }
            }
    }

    private void retroDownload(final File target, int index) {
        RestApiImpl restApi = new RestApiImpl();
//        restApi.download(index).subscribe(new Subscriber<ResponseBody>() {
//            @Override
//            public void onCompleted() {
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(ResponseBody responseBody) {
//                try {
//                    InputStream inputStream = null;
//                    OutputStream outputStream = null;
//                    try {
//                        byte[] fileReader = new byte[4096];
//                        long fileSize = responseBody.contentLength();
//                        long fileSizeDownloaded = 0;
//                        inputStream = responseBody.byteStream();
//                        outputStream = new FileOutputStream(target);
//                        while (true) {
//                            int read = inputStream.read(fileReader);
//                            if (read == -1)
//                                break;
//                            outputStream.write(fileReader, 0, read);
//                            fileSizeDownloaded += read;
//                            Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
//                        }
//                        outputStream.flush();
//                        rxEventBus.send("file Downloaded!");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (inputStream != null)
//                            inputStream.close();
//                        if (outputStream != null)
//                            outputStream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}