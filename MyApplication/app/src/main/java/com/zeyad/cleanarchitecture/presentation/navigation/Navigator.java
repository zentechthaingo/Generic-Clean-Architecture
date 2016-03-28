package com.zeyad.cleanarchitecture.presentation.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zeyad.cleanarchitecture.presentation.views.activities.UserDetailsActivity;
import com.zeyad.cleanarchitecture.presentation.views.activities.UserListActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class used to navigate through the application.
 */
//@Singleton
public class Navigator {

//    @Inject
    public Navigator() {
        //empty
    }

    /**
     * Goes to the user list screen.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToUserList(Context context) {
        if (context != null) {
            Intent intentToLaunch = UserListActivity.getCallingIntent(context);
            context.startActivity(intentToLaunch);
        }
    }

    /**
     * Goes to the user details screen.
     *
     * @param context A Context needed to open the destiny activity.
     */
    public void navigateToUserDetails(Context context, int userId, Bundle bundle) {
        if (context != null) {
            Intent intentToLaunch = UserDetailsActivity.getCallingIntent(context, userId);
            if (bundle == null)
                context.startActivity(intentToLaunch);
            else context.startActivity(intentToLaunch, bundle);
        }
    }
}