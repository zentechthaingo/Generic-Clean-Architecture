package com.zeyad.cleanarchitecturet.presentation.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.zeyad.cleanarchitecturet.R;
import com.zeyad.cleanarchitecturet.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.DaggerUserComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.modules.UserModule;
import com.zeyad.cleanarchitecturet.presentation.views.fragments.UserDetailsFragment;

/**
 * Activity that shows details of a certain user.
 */
public class UserDetailsActivity extends BaseActivity implements HasComponent<UserComponent> {

    private static final String INTENT_EXTRA_PARAM_USER_ID = "org.android10.INTENT_PARAM_USER_ID";
    private static final String INSTANCE_STATE_PARAM_USER_ID = "org.android10.STATE_PARAM_USER_ID";

    private int userId;
    private UserComponent userComponent;

    public static Intent getCallingIntent(Context context, int userId) {
        Intent callingIntent = new Intent(context, UserDetailsActivity.class);
        callingIntent.putExtra(INTENT_EXTRA_PARAM_USER_ID, userId);
        return callingIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initializeActivity(savedInstanceState);
        initializeInjector();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null)
            outState.putInt(INSTANCE_STATE_PARAM_USER_ID, userId);
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes this activity.
     */
    private void initializeActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            userId = getIntent().getIntExtra(INTENT_EXTRA_PARAM_USER_ID, -1);
            addFragment(R.id.fl_fragment, UserDetailsFragment.newInstance(userId));
        } else
            userId = savedInstanceState.getInt(INSTANCE_STATE_PARAM_USER_ID);
    }

    private void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .userModule(new UserModule(userId))
                .build();
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }
}