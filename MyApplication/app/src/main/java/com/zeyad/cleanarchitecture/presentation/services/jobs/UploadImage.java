package com.zeyad.cleanarchitecture.presentation.services.jobs;

import android.content.Intent;

import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;

/**
 * @author Zeyad on 6/05/16.
 */
public class UploadImage {
    public static final String TAG = UploadImage.class.getSimpleName();
    RxEventBus rxEventBus;

    public UploadImage(Intent intent, RxEventBus rxEventBus) {
        this.rxEventBus = rxEventBus;

    }
}