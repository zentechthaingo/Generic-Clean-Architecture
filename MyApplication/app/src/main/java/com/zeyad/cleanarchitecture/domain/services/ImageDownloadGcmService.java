package com.zeyad.cleanarchitecture.domain.services;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: 3/30/16 Finish!
public class ImageDownloadGcmService extends GcmTaskService {

    public static final String TAG = ImageDownloadIntentService.class.getSimpleName(),
            EXTRA_PRODUCTS = "products",
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

//    @Override
//    public int onRunTask(TaskParams taskParams) {
//        Constants.CACHE_DIR = new File(String.valueOf(getCacheDir())).getAbsolutePath();
//        CACHE_DIR = Constants.CACHE_DIR;
//        this.dir = new File(CACHE_DIR);
//        File lockSignature = new File(dir, "dl.lock");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        } else {
//            if (lockSignature.exists()) {
//                // TODO: check signature
//            } else {
//                // TODO: generate signature
//            }
//        }
//        addAllCachedFiles();
//        MyApp.getComponent(this).inject(this);
//        bmOptions = new BitmapFactory.Options();
//        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
//        Products products = new Gson().fromJson(taskParams.getExtras().getString(EXTRA_PRODUCTS), Products.class);
//        File target;
//        String url;
//        String cat;
//        String name;
//        String filter;
//        Intent localIntent;
//        boolean localCopy;
//        String targetPath;
//        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        for (Product product : products.getProducts()) {
//            url = product.download();
//            name = product.getName().toLowerCase() + ".jpg";
//            cat = taskParams.getExtras().getString(EXTRA_CATEGORY);
//            filter = taskParams.getExtras().getString(EXTRA_FILTER_SCHEME);
//            localIntent = new Intent(DOWNLOAD_STATUS_CHANGED + "-" + filter).putExtra(EXTENDED_DATA_KEY,
//                    url);
//            localCopy = false;
//            if (url != null && url.startsWith(Constants.CACHE_DIR))
//                if (new File(url).exists()) {
//                    url = url.replaceFirst(Constants.CACHE_DIR, "");
//                    downloadedKeys.add(url);
//                    localCopy = true;
//                }
//            target = new File(dir, name);// check here
//            targetPath = target.getAbsolutePath();
//            if (downloadedKeys.contains(url)) {
//                if (localCopy) {
//                    targetPath = Constants.CACHE_DIR + url;
//                    Log.d(TAG, "Found in wallet " + url + ", file " + targetPath);
//                } else {
//                    Log.d(TAG, "Found in cache " + url + ", file " + targetPath);
//                }
//                localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_COMPLETED)
//                        .putExtra(EXTENDED_DATA_FILE_PATH, targetPath);
//            } else {
//                if (target.exists()) {
//                    downloadedKeys.add(url);
//                } else {
//                    Log.d(TAG, "Downloading " + url + " into " + targetPath);
//                    try {
////                        download(target, url);
//                        download(target, url.substring(url.lastIndexOf("/") + 1, url.length() - 4));
//                        downloadedKeys.add(url);
//                        if (cat != null && !cat.isEmpty()) {
//                            if (!categorizedKeys.containsKey(cat))
//                                categorizedKeys.put(cat, new ArrayList<String>());
//                            categorizedKeys.getById(cat).add(url);
//                        }
//                        localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_COMPLETED)
//                                .putExtra(EXTENDED_DATA_FILE_PATH, targetPath);
//                    } catch (Exception e) {
//                        target = new File(targetPath);
//                        if (target.exists()) {
//                            target.delete();
//                            Log.e(TAG, "Delete corrupted file");
//                        }
//                        e.printStackTrace();
//                        localIntent.putExtra(EXTENDED_DATA_STATUS, EXTENDED_DATA_STATUS_FAILED);
//                    }
//                }
//            }
//            localBroadcastManager.sendBroadcast(localIntent);
//        }
//        return GcmNetworkManager.RESULT_SUCCESS;
//    }

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

    private void download(final File target, String index) {
//        try {
//            FileOutputStream fOut = new FileOutputStream(target);
//            Glide.with(getById())
//                    .load(imageUrl)
//                    .asBitmap()
//                    .into(intent.getIntExtra(WIDTH, 100), intent.getIntExtra(HEIGHT, 100))
//                    .getById()
//                    .compress(Bitmap.CompressFormat.PNG, 85, fOut);
//            fOut.flush();
//            fOut.close();
//        } catch (InterruptedException | ExecutionException | IOException e) {
//            e.printStackTrace();
//            Log.e(TAG, e.getMessage());
//        }
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        return 0;
    }
}