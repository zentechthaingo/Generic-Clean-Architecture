package com.zeyad.cleanarchetecturet.utilities;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class Utils {
    // TODO: 1/5/16 Test!
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context, JobInfo jobInfo) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler.schedule(jobInfo) == 1)
            Log.d("JobScheduler", "Job scheduled successfully!");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }
        return false;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

//    // TODO: 1/9/16 Test!
//    public static void RxCacheImages(final Products products, final RestAdapter restAdapter) {
//        Observable.create(new Observable.OnSubscribe<File>() {
//            @Override
//            public void call(final Subscriber<? super File> subscriber) {
//                for (final Product product : products.getProducts())
//                    if (product.getImage() != null && product.getName() != null) {
//                        restAdapter.create(Controllers.getImages.class).getStream(product.getImage()
//                                .substring(product.getImage().lastIndexOf("/") + 1, product.getImage().length() - 4), new retrofit.Callback<Response>() {
//                            @Override
//                            public void success(final Response response, Response response2) {
//                                Observable.create(new Observable.OnSubscribe<File>() {
//                                    @Override
//                                    public void call(Subscriber<? super File> subscriber) {
//                                        try {
//                                            File target = new File(product.getName().toLowerCase() + ".jpg");
//                                            InputStream input = response.getBody().in();
//                                            int count;
//                                            OutputStream output = new FileOutputStream(target);
//                                            byte data[] = new byte[1024];
//                                            while ((count = input.read(data)) != -1)
//                                                output.write(data, 0, count);
//                                            output.flush();
//                                            output.close();
//                                            input.close();
//                                            subscriber.onNext(target); // Emit the contents of the URL
//                                            subscriber.onCompleted(); // Nothing more to emit
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                            subscriber.onError(e); // In case there are network errors
//                                        }
//                                    }
//                                }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(new Observer<File>() {
//                                    @Override
//                                    public void onCompleted() { // Called when the observable has no more data to emit
//                                        Log.d("Rx image caching", "Image download complete");
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {// Called when the observable encounters an error
//                                        Log.d("Rx image caching", "Failed to download image", e);
//                                        subscriber.onError(e);
//                                    }
//
//                                    @Override
//                                    public void onNext(File file) {// Called each time the observable emits data
//                                        Log.d("Rx image caching", "Image download successful, name: " + file.getName());
//                                        subscriber.onNext(file);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void failure(final RetrofitError error) {
////                                Toast.makeText(ImageDownloadGcmService.this, getString(R.string.please_check_internet),
////                                        Toast.LENGTH_SHORT).show();
//                                Log.e("fail", error.toString());
//                            }
//                        });
////                        try {
////                            File target = new File(product.getName().toLowerCase());
////                            int count;
////                            URL url = new URL(product.getImage());
////                            URLConnection connection = url.openConnection();
////                            connection.connect();
////                            InputStream input = new BufferedInputStream(url.openStream());
////                            OutputStream output = new FileOutputStream(target); // remove the leading slash.
////                            byte data[] = new byte[1024];
////                            while ((count = input.read(data)) != -1)
////                                output.write(data, 0, count);
////                            output.flush();
////                            output.close();
////                            input.close();
////                            subscriber.onNext(target); // Emit the contents of the URL
////                        } catch (Exception e) {
////                            subscriber.onError(e); // In case there are network errors
////                        }
//                    }
//                subscriber.onCompleted(); // Nothing more to emit
//            }
//        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<File>() {
//            @Override
//            public void onCompleted() { // Called when the observable has no more data to emit
//                Log.d("Rx image caching", "Image download complete");
//            }
//
//            @Override
//            public void onError(Throwable e) {// Called when the observable encounters an error
//                Log.d("Rx image caching", "Failed to download image", e);
//            }
//
//            @Override
//            public void onNext(File file) {// Called each time the observable emits data
//                Log.d("Rx image caching", "Image download successful, name: " + file.getName());
//            }
//        });
//    }
}