package com.zeyad.cleanarchetecturet.presentation.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.zeyad.cleanarchetecturet.R;
import com.zeyad.cleanarchetecturet.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchetecturet.presentation.internal.di.components.DaggerUserComponent;
import com.zeyad.cleanarchetecturet.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchetecturet.presentation.model.UserModel;
import com.zeyad.cleanarchetecturet.presentation.view.fragments.UserListFragment;

/**
 * Activity that shows a list of Users.
 */
public class UserListActivity extends BaseActivity implements HasComponent<UserComponent>,
        UserListFragment.UserListListener {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    private UserComponent userComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_user_list);
        initializeInjector();
    }

    private void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }

    @Override
    public void onUserClicked(UserModel userModel) {
        navigator.navigateToUserDetails(this, userModel.getUserId());
    }
}