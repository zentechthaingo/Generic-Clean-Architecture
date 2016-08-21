package com.zeyad.cleanarchitecture.presentation.navigation;

import android.content.Context;
import android.content.Intent;

import com.zeyad.cleanarchitecture.presentation.screens.BaseActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class used to navigate through the application.
 */
@Singleton
public class Navigator {

    @Inject
    public Navigator() {
        // empty
    }

    public void navigateTo(Context context, Intent intent) {
        context.startActivity(intent);
    }

    public void navigateToForResult(BaseActivity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }
}