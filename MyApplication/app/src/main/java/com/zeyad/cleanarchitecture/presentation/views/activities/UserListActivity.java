package com.zeyad.cleanarchitecture.presentation.views.activities;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;
import com.zeyad.cleanarchitecture.presentation.views.adapters.UsersAdapter;
import com.zeyad.cleanarchitecture.presentation.views.fragments.UserDetailsFragment;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private SearchView searchView;
    private Subscription fabSubscription = null;
    private UsersAdapter usersAdapter;
    private UsersAdapter.OnItemClickListener onItemClickListener = new UsersAdapter.OnItemClickListener() {
        @Override
        public void onUserItemClicked(UserModel userModel, UserViewHolder holder) {
            if (userListPresenter != null && userModel != null)
                userListPresenter.onUserClicked(userModel, holder);
        }

        @Override
        public void onUserItemClicked(int position) {
            if (actionMode != null)
                toggleSelection(position);
        }

        @Override
        public boolean onItemLongClicked(int position) {
            if (actionMode == null)
                actionMode = startSupportActionMode(actionModeCallback);
            toggleSelection(position);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initializeInjector();
        initialize();
        setupUI();
        loadUserList();
        if (savedInstanceState != null)
            rv_users.scrollToPosition(savedInstanceState.getInt(STATE_SCROLL));
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

    // TODO: 4/3/16 Add fab shared elements!
    @Override
    public void onResume() {
        super.onResume();
        userListPresenter.resume();
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

    private void initialize() {
        getComponent(UserComponent.class).inject(this);
        userListPresenter.setView(this);
    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) this).getComponent());
    }

    private void setupUI() {
        ButterKnife.bind(this);
//        onSearchRequested();
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        }
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());
        if (findViewById(R.id.detail_container) != null) // Two pane for tablets(res/values-w900dp).
            mTwoPane = true;
        rv_users.setLayoutManager(new LinearLayoutManager(this));
        usersAdapter = new UsersAdapter(this, new ArrayList<>());
        usersAdapter.setOnItemClickListener(onItemClickListener);
        rv_users.setAdapter(usersAdapter);
        rv_users.setItemAnimator(new DefaultItemAnimator());
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
        if (userModelCollection != null) {
            usersAdapter.setUsersCollection(userModelCollection);
            usersAdapter.animateTo((List<UserModel>) userModelCollection);
            rv_users.scrollToPosition(0);
        }
    }

    // TODO: 3/27/16 Add Animation!
    @Override
    public void viewUser(UserModel userModel, UserViewHolder holder) {
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
            if (Utils.hasLollipop()) {
                Pair<View, String> pair = new Pair<>(holder.getmAvatar(), holder.getmAvatar().getTransitionName());
                Pair<View, String> secondPair = new Pair<>(holder.getTextViewTitle(),
                        holder.getTextViewTitle().getTransitionName());
                navigator.navigateToUserDetails(this, userModel.getUserId(),
                        ActivityOptions.makeSceneTransitionAnimation(this, pair, secondPair).toBundle());
            } else
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
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // Define the listener
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        };
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), expandListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_search).setVisible(true).setEnabled(true);
        menu.findItem(R.id.delete_item).setVisible(false).setEnabled(false);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                usersAdapter.removeItems(usersAdapter.getSelectedItems());
                return true;
            case R.id.menu_search:
                userListPresenter.search(searchView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //---------------------------------------------------------//

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    /**
     * Toggle the selection state of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        usersAdapter.toggleSelection(position);
        int count = usersAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.list_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_item:
                    usersAdapter.removeItems(usersAdapter.getSelectedItems());
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            usersAdapter.clearSelection();
            actionMode = null;
        }
    }
}