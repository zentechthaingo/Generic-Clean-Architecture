package com.zeyad.cleanarchitecture.presentation.views.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.services.GenericNetworkQueueIntentService;
import com.zeyad.cleanarchitecture.utilities.Utils;

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
        } else return this;
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
            getContext().startService(new Intent(getContext(), GenericNetworkQueueIntentService.class)
                    .putExtra(GenericNetworkQueueIntentService.EXTRA_REMOTE_PATH, imageUrl)
                    .putExtra(GenericNetworkQueueIntentService.EXTRA_REMOTE_NAME, Utils.getFileNameFromUrl(imageUrl))
                    .putExtra(GenericNetworkQueueIntentService.WIDTH, getWidth())
                    .putExtra(GenericNetworkQueueIntentService.HEIGHT, getHeight()));
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
//                    .override(getWidth(), getHeight())
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(this);
        else if (channel.equalsIgnoreCase(DISK))
            Glide.with(getContext().getApplicationContext())
                    .load(Uri.fromFile(img))
                    .placeholder(imagePlaceHolderResourceId)
                    .fallback(imageFallBackResourceId)
                    .error(imageOnErrorResourceId)
//                    .override(getWidth(), getHeight())
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

    // TODO: 3/24/16 Find more efficient way!
    public Bitmap getBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
        return (bitmapDrawable.getBitmap() != null) ? bitmapDrawable.getBitmap() : null;

//        Bitmap bitmap;
//        Drawable drawable = getDrawable();
//        if (drawable instanceof BitmapDrawable) {
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//            if (bitmapDrawable.getBitmap() != null)
//                return bitmapDrawable.getBitmap();
//        }
//        if (getWidth() <= 0 || getHeight() <= 0)
//            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
//        else
//            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, getWidth(), getHeight());
//        drawable.draw(canvas);
//        return bitmap;
    }
}