package com.zeyad.cleanarchitecture.presentation.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jakewharton.rxbinding.view.RxView;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.DaggerUserComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.UserModule;
import com.zeyad.cleanarchitecture.presentation.views.component.AutoLoadImageView;
import com.zeyad.cleanarchitecture.presentation.views.fragments.UserDetailsFragment;
import com.zeyad.cleanarchitecture.utilities.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Activity that shows details of a certain user.
 */
public class UserDetailsActivity extends BaseActivity implements HasComponent<UserComponent> {

    private static final String INTENT_EXTRA_PARAM_USER_ID = "org.android10.INTENT_PARAM_USER_ID",
            INSTANCE_STATE_PARAM_USER_ID = "org.android10.STATE_PARAM_USER_ID", FAB_EDIT_TAG = "edit",
            FAB_ADD_TAG = "done";
    @Bind(R.id.detail_toolbar)
    public Toolbar mToolbar;
    @Bind(R.id.coordinator_layout)
    public CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.detail_image)
    public AutoLoadImageView mDetailImage;
    @Bind(R.id.edit_details_fab)
    FloatingActionButton editDetailsFab;
    private Subscription fabSubscription;
    private int userId;
    private UserComponent userComponent;

    public static Intent getCallingIntent(Context context, int userId) {
        Intent callingIntent = new Intent(context, UserDetailsActivity.class);
        callingIntent.putExtra(INTENT_EXTRA_PARAM_USER_ID, userId);
        return callingIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initializeActivity(savedInstanceState);
        initializeInjector();
        editDetailsFab.setTag(FAB_EDIT_TAG);
        fabSubscription = RxView.clicks(editDetailsFab)
                .subscribe(aVoid -> {
                    if (editDetailsFab.getTag().equals(FAB_EDIT_TAG)) {
                        editDetailsFab.setTag(FAB_ADD_TAG);
                        editDetailsFab.setImageResource(R.drawable.ic_done);
                        ((UserDetailsFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.user_detail_container))
                                .getUserDetailsPresenter()
                                .setupEdit();
                    } else {
                        editDetailsFab.setTag(FAB_EDIT_TAG);
                        editDetailsFab.setImageResource(R.drawable.ic_edit);
                        ((UserDetailsFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.user_detail_container))
                                .getUserDetailsPresenter()
                                .submitEdit();
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null)
            outState.putInt(INSTANCE_STATE_PARAM_USER_ID, userId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            supportFinishAfterTransition(); // exit animation
            navigateUpTo(new Intent(this, UserListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes this activity.
     */
    private void initializeActivity(Bundle savedInstanceState) {
        if (savedInstanceState == null)
            userId = getIntent().getIntExtra(INTENT_EXTRA_PARAM_USER_ID, -1);
        else
            userId = savedInstanceState.getInt(INSTANCE_STATE_PARAM_USER_ID);
        ButterKnife.bind(this);
//        setSupportActionBar(mToolbar);
//        mToolbar.setTitle(getTitle());
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        if (savedInstanceState == null)
            addFragment(R.id.user_detail_container, UserDetailsFragment.newInstance(userId));
    }

    private void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .userModule(new UserModule(userId))
                .build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.unsubscribeIfNotNull(fabSubscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }

    public FloatingActionButton getEditDetailsFab() {
        return editDetailsFab;
    }
}