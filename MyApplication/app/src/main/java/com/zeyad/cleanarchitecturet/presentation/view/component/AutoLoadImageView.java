package com.zeyad.cleanarchitecturet.presentation.view.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zeyad.cleanarchitecturet.R;
import com.zeyad.cleanarchitecturet.utilities.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
// FIXME: 3/6/16 !

/**
 * Simple implementation of {@link ImageView} with extended features like setting an
 * image from an url and an internal file cache using the application cache directory.
 */
public class AutoLoadImageView extends ImageView {

    private static final String TAG = "AutoLoadImageView", BASE_IMAGE_NAME_CACHED = "image_",
            CLOUD = "Cloud", DISK = "Disk";
    private String imageUrl;
    private File img;
    private int imagePlaceHolderResourceId = -1, imageOnErrorResourceId = -1, imageFallBackResourceId = -1;


    public AutoLoadImageView(Context context) {
        super(context);
    }

    public AutoLoadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLoadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Set an image from a remote url.
     *
     * @param imageUrl The url of the resource to load.
     */
    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
        if (imageUrl != null)
            loadImageFromUrl(imageUrl);
        else throw new NullPointerException();
        imagePlaceHolderResourceId = imageOnErrorResourceId = imageFallBackResourceId = R.drawable.placer_holder_img;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public void setImagePlaceHolder(int resourceId) {
        imagePlaceHolderResourceId = resourceId;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public void setImageFallBackResourceId(int resourceId) {
        imageFallBackResourceId = resourceId;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public void setImageOnErrorResourceId(int resourceId) {
        imageOnErrorResourceId = resourceId;
    }

    /**
     * Loads and image from the internet (and cache it) or from the internal cache.
     *
     * @param imageUrl The remote image url to load.
     */
    private void loadImageFromUrl(final String imageUrl) {
        // FIXME: 3/6/16 fix string path!
        img = buildFileFromFilename(getFileNameFromUrl(imageUrl));
        if (!img.exists()) {
            loadBitmap(CLOUD);
            Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    put(getFileNameFromUrl(imageUrl));
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Void>() {
                @Override
                public void onCompleted() { // Called when the observable has no more data to emit
                    Log.d("Rx image caching", "Image download complete");
                }

                @Override
                public void onError(Throwable e) {// Called when the observable encounters an error
                    Log.d("Rx image caching", "Failed to download image", e);
                }

                @Override
                public void onNext(Void nA) {// Called each time the observable emits data
                    Log.d("Rx image caching", "Image download successful, name: " + img.getName());
                }
            });
//            Intent intent = new Intent(getContext(), ImageDownloadIntentService.class);
//            getContext().startService(intent.putExtra(ImageDownloadIntentService.EXTRA_REMOTE_PATH, imageUrl)
//                    .putExtra(ImageDownloadIntentService.EXTRA_REMOTE_NAME, getFileNameFromUrl(imageUrl)));
        }
    }

    /**
     * Run the operation of loading a bitmap on the UI thread.
     *
     * @param channel The channel of retrieving the bitmap, Cloud or local.
     */
    private void loadBitmap(String channel) {
        if (channel.equalsIgnoreCase(CLOUD))
            Glide.with(getContext().getApplicationContext())
                    .load(imageUrl)
                    .placeholder(imagePlaceHolderResourceId)
                    .fallback(imageFallBackResourceId)
                    .error(imageOnErrorResourceId)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(this);
        else if (channel.equalsIgnoreCase(DISK))
            Glide.with(getContext().getApplicationContext())
                    .load(Uri.fromFile(img))
                    .placeholder(imagePlaceHolderResourceId)
                    .fallback(imageFallBackResourceId)
                    .error(imageOnErrorResourceId)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(this);
    }

    /**
     * Creates a file name from an image url
     *
     * @param imageUrl The image url used to build the file name.
     * @return An String representing a unique file name.
     */
    private String getFileNameFromUrl(String imageUrl) {
        //we could generate an unique MD5/SHA-1 here
        String hash = String.valueOf(imageUrl.hashCode());
        if (hash.startsWith("-"))
            hash = hash.substring(1);
        return BASE_IMAGE_NAME_CACHED + hash;
    }

    /**
     * Cache an element.
     *
     * @param fileName A string representing the name of the file to be cached.
     */
    void put(String fileName) {
        try {
            File dir = new File(Constants.CACHE_DIR);
            if (!dir.exists())
                dir.mkdirs();
            img = buildFileFromFilename(fileName);
            if (!img.exists()) {
                FileOutputStream fOut = new FileOutputStream(img);
                Glide.with(getContext().getApplicationContext())
                        .load(imageUrl)
                        .asBitmap()
                        .into(getWidth(), getHeight())
                        .get()
                        .compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            }

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Invalidate and expire the cache.
     */
    void evictAll(File cacheDir) {
        for (File file : cacheDir.listFiles())
            file.delete();
    }

    /**
     * Creates a file name from an image url
     *
     * @param fileName The image url used to build the file name.
     * @return A {@link File} representing a unique element.
     */
    private File buildFileFromFilename(String fileName) {
        String fullPath = Constants.CACHE_DIR + File.separator + fileName;
        return new File(fullPath);
    }
}