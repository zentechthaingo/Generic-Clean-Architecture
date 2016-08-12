package com.zeyad.cleanarchitecture.presentation.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zeyad.cleanarchitecture.presentation.screens.users.details.UserDetailsActivity;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserListActivity;

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