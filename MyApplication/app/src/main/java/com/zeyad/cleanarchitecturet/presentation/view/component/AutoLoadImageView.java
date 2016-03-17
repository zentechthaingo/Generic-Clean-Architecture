package com.zeyad.cleanarchitecturet.presentation.view.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zeyad.cleanarchitecturet.R;
import com.zeyad.cleanarchitecturet.presentation.view.services.ImageDownloadIntentService;
import com.zeyad.cleanarchitecturet.utilities.Utils;

import java.io.File;

/**
 * Simple implementation of {@link ImageView} with extended features like setting an
 * image from an url and an internal file cache using the application cache directory.
 */
public class AutoLoadImageView extends ImageView {

    private static final String TAG = "AutoLoadImageView", CLOUD = "Cloud", DISK = "Disk";
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
    public AutoLoadImageView setImageUrl(final String imageUrl) {
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
            loadImageFromUrl(imageUrl);
        } else throw new NullPointerException();
        imagePlaceHolderResourceId = imageOnErrorResourceId = imageFallBackResourceId = R.drawable.placer_holder_img;
        return this;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public AutoLoadImageView setImagePlaceHolder(int resourceId) {
        imagePlaceHolderResourceId = resourceId;
        return this;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public AutoLoadImageView setImageFallBackResourceId(int resourceId) {
        imageFallBackResourceId = resourceId;
        return this;
    }

    /**
     * Set a place holder used for loading when an image is being downloaded from the internet.
     *
     * @param resourceId The resource id to use as a place holder.
     */
    public AutoLoadImageView setImageOnErrorResourceId(int resourceId) {
        imageOnErrorResourceId = resourceId;
        return this;
    }

    /**
     * Loads and image from the internet (and cache it) or from the internal cache.
     *
     * @param imageUrl The remote image url to load.
     */
    private void loadImageFromUrl(final String imageUrl) {
        img = Utils.buildFileFromFilename(Utils.getFileNameFromUrl(imageUrl));
        if (img.exists())
            loadBitmap(DISK);
        else {
            loadBitmap(CLOUD);
            Intent intent = new Intent(getContext(), ImageDownloadIntentService.class);
            getContext().startService(intent.putExtra(ImageDownloadIntentService.EXTRA_REMOTE_PATH, imageUrl)
                    .putExtra(ImageDownloadIntentService.EXTRA_REMOTE_NAME, Utils.getFileNameFromUrl(imageUrl))
                    .putExtra(ImageDownloadIntentService.WIDTH, getWidth())
                    .putExtra(ImageDownloadIntentService.HEIGHT, getHeight()));
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
                    .override(getWidth(), getHeight())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(this);
        else if (channel.equalsIgnoreCase(DISK))
            Glide.with(getContext().getApplicationContext())
                    .load(Uri.fromFile(img))
                    .placeholder(imagePlaceHolderResourceId)
                    .fallback(imageFallBackResourceId)
                    .error(imageOnErrorResourceId)
                    .override(getWidth(), getHeight())
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(this);
    }

    /**
     * Invalidate and expire the cache.
     */
    public AutoLoadImageView evictAll(File cacheDir) {
        for (File file : cacheDir.listFiles())
            file.delete();
        return this;
    }
}