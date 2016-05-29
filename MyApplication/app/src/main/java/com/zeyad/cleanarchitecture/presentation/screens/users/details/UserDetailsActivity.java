package com.zeyad.cleanarchitecture.presentation.screens.users.details;

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
import com.zeyad.cleanarchitecture.presentation.components.AutoLoadImageView;
import com.zeyad.cleanarchitecture.presentation.screens.BaseActivity;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserListActivity;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity that shows details of a certain user.
 */
public class UserDetailsActivity extends BaseActivity {

    private static final String INTENT_EXTRA_PARAM_USER_ID = "INTENT_PARAM_USER_ID",
            INSTANCE_STATE_PARAM_USER_ID = "STATE_PARAM_USER_ID", FAB_EDIT_TAG = "edit",
            FAB_ADD_TAG = "done";
    @Bind(R.id.detail_toolbar)
    public Toolbar mToolbar;
    @Bind(R.id.coordinator_layout)
    public CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.detail_image)
    public AutoLoadImageView mDetailImage;
    @Bind(R.id.edit_details_fab)
    FloatingActionButton editDetailsFab;
    private int userId;

    public static Intent getCallingIntent(Context context, int userId) {
        return new Intent(context, UserDetailsActivity.class).putExtra(INTENT_EXTRA_PARAM_USER_ID, userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            userId = getIntent().getIntExtra(INTENT_EXTRA_PARAM_USER_ID, -1);
            addFragment(R.id.user_detail_container, UserDetailsFragment.newInstance(userId), new ArrayList<>());
        } else
            userId = savedInstanceState.getInt(INSTANCE_STATE_PARAM_USER_ID);

    }

    @Override
    public void initialize() {
    }

    @Override
    public void setupUI() {
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);
        // Show the Up button in the action bar.
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        editDetailsFab.setTag(FAB_EDIT_TAG);
        mCompositeSubscription.add(RxView.clicks(editDetailsFab)
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
                }));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null)
            outState.putInt(INSTANCE_STATE_PARAM_USER_ID, userId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition(); // exit animation
            navigateUpTo(new Intent(this, UserListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    public FloatingActionButton getEditDetailsFab() {
        return editDetailsFab;
    }
}