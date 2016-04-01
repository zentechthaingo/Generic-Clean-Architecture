package com.zeyad.cleanarchitecture.presentation.views.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.annimations.DetailsTransition;
import com.zeyad.cleanarchitecture.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.DaggerUserComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.presenters.GenericListPresenter;
import com.zeyad.cleanarchitecture.presentation.views.UserListView;
import com.zeyad.cleanarchitecture.presentation.views.adapters.UsersAdapter;
import com.zeyad.cleanarchitecture.presentation.views.fragments.UserDetailsFragment;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Activity that shows a list of Users.
 */
public class UserListActivity extends BaseActivity implements HasComponent<UserComponent>, UserListView {

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    private boolean mTwoPane;
    private UserComponent userComponent;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    //--------------- Migration stuff --------------------------//
    private int mScrollPosition = -1;
    private static final String STATE_SCROLL = "scrollPosition";
    @Inject
    GenericListPresenter userListPresenter;
    @Bind(R.id.rv_users)
    RecyclerView rv_users;
    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;
    @Bind(R.id.rl_retry)
    RelativeLayout rl_retry;
    @Bind(R.id.bt_retry)
    Button bt_retry;
    @Bind(R.id.fab_add)
    FloatingActionButton addFab;
    private Subscription fabSubscription;
    private UsersAdapter usersAdapter;
    private UsersAdapter.OnItemClickListener onItemClickListener =
            userModel -> {
                if (userListPresenter != null && userModel != null)
                    userListPresenter.onUserClicked(userModel);
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initializeInjector();
        initialize();
        setupUI();
        loadUserList();
        if (savedInstanceState != null) {
            mScrollPosition = savedInstanceState.getInt(STATE_SCROLL);
            rv_users.scrollToPosition(mScrollPosition);
        }
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putInt(STATE_SCROLL, rv_users.getVerticalScrollbarPosition());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        userListPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        userListPresenter.pause();
        Utils.unsubscribeIfNotNull(fabSubscription);
        Utils.unsubscribeIfNotNull(usersAdapter.getItemSubscription());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        userListPresenter.destroy();
    }

    private void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    // FIXME: 3/26/16 Test!
    private void initialize() {
        getComponent(UserComponent.class).inject(this);
        userListPresenter.setView(this);
    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) this).getComponent());
    }

    private void setupUI() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());
        if (findViewById(R.id.detail_container) != null) // Two pane for tablets(res/values-w900dp).
            mTwoPane = true;
        rv_users.setLayoutManager(new LinearLayoutManager(this));
        usersAdapter = new UsersAdapter(this, new ArrayList<>());
        usersAdapter.setOnItemClickListener(onItemClickListener);
        rv_users.setAdapter(usersAdapter);
        fabSubscription = RxView.clicks(addFab).subscribe(aVoid -> {
            if (mTwoPane) {
                UserDetailsFragment fragment = UserDetailsFragment.newInstance(-1);
                if (Utils.hasLollipop()) {
                    fragment.setSharedElementEnterTransition(new DetailsTransition());
                    fragment.setEnterTransition(new Fade());
                    fragment.setExitTransition(new Fade());
                    fragment.setSharedElementReturnTransition(new DetailsTransition());
                }
                Bundle arguments = new Bundle();
                arguments.putBoolean(UserDetailsFragment.ADD_NEW_ITEM, true);
                fragment.setArguments(arguments);
                addFragment(R.id.detail_container, fragment);
            } else navigator.navigateToUserDetails(this, -1, null);
        });
    }

    @Override
    public void showLoading() {
        rl_progress.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        rl_progress.setVisibility(View.GONE);
        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void showRetry() {
        rl_retry.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetry() {
        rl_retry.setVisibility(View.GONE);
    }

    @Override
    public void renderUserList(Collection<UserModel> userModelCollection) {
        if (userModelCollection != null)
            usersAdapter.setUsersCollection(userModelCollection);
    }

    // TODO: 3/27/16 Add Animation!
    @Override
    public void viewUser(UserModel userModel) {
        if (mTwoPane) {
            UserDetailsFragment fragment = new UserDetailsFragment();
            if (Utils.hasLollipop()) {
                fragment.setSharedElementEnterTransition(new DetailsTransition());
                fragment.setEnterTransition(new Fade());
                fragment.setExitTransition(new Fade());
                fragment.setSharedElementReturnTransition(new DetailsTransition());
            }
            Bundle arguments = new Bundle();
            arguments.putInt(UserDetailsFragment.ARG_ITEM_ID, userModel.getUserId());
            arguments.putString(UserDetailsFragment.ARG_ITEM_IMAGE, userModel.getCoverUrl());
            arguments.putString(UserDetailsFragment.ARG_ITEM_NAME, userModel.getFullName());
            fragment.setArguments(arguments);
            addFragment(R.id.detail_container, fragment);
        } else {
//            Intent intent = new Intent(mContext, UserDetailsFragment.class);
//            intent.putExtra(UserDetailsFragment.ARG_ITEM_ID, holder.mProduct.getProduct_id());
//            intent.putExtra(UserDetailsFragment.ARG_ITEM_IMAGE, holder.mProduct.getImage());
//            intent.putExtra(UserDetailsFragment.ARG_ITEM_NAME, holder.mProduct.getName()
//                    .toLowerCase());
//            if (Utils.hasLollipop()) {
//                Pair<View, String> pair = new Pair<>(holder.mImageView, holder.mImageView.getTransitionName());
//                Pair<View, String> secondPair = new Pair<>(holder.mPriceView, holder.mPriceView.getTransitionName());
//                navigator.navigateToUserDetails(this, userModel.getUserId(), ActivityOptions.makeSceneTransitionAnimation(this, pair, secondPair).toBundle());
//            } else
            navigator.navigateToUserDetails(this, userModel.getUserId(), null);
        }
    }

    @Override
    public void showError(String message) {
        showToastMessage(message);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    /**
     * Loads all users.
     */
    private void loadUserList() {
        userListPresenter.initialize();
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        loadUserList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.delete_icon).setVisible(false).setEnabled(false);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_icon:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}