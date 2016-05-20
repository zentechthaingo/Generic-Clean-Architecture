package com.zeyad.cleanarchitecture.presentation.screens.users.list;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.presentation.annimations.DetailsTransition;
import com.zeyad.cleanarchitecture.presentation.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.cleanarchitecture.presentation.components.adapter.ItemInfo;
import com.zeyad.cleanarchitecture.presentation.components.adapter.RecyclerViewFooterViewHolder;
import com.zeyad.cleanarchitecture.presentation.components.adapter.RecyclerViewHeaderViewHolder;
import com.zeyad.cleanarchitecture.presentation.components.adapter.RecyclerViewLoadingViewHolder;
import com.zeyad.cleanarchitecture.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.DaggerUserComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecture.presentation.screens.BaseActivity;
import com.zeyad.cleanarchitecture.presentation.screens.GenericListView;
import com.zeyad.cleanarchitecture.presentation.screens.users.details.UserDetailsFragment;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Activity that shows a list of Users.
 */
public class UserListActivity extends BaseActivity implements HasComponent<UserComponent>,
        GenericListView<UserViewModel, UserViewHolder>, ActionMode.Callback {

    private static final String TAG = UserListActivity.class.getSimpleName(), STATE_SCROLL = "scrollPosition";
    private boolean mTwoPane;
    private UserComponent userComponent;
    @Inject
    UserListPresenter mUserListPresenter;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rv_users)
    RecyclerView rv_users;
    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;
    @Bind(R.id.rl_retry)
    RelativeLayout rl_retry;
    @Bind(R.id.bt_retry)
    Button bt_retry;
    @Bind(R.id.fab_add)
    FloatingActionButton mAddFab;
    private List<Pair<View, String>> mSharedElements;
    private ActionMode actionMode;
    private GenericRecyclerViewAdapter mUsersAdapter;
    private GenericRecyclerViewAdapter.OnItemClickListener onItemClickListener = new GenericRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(int position, ItemInfo userViewModel, GenericRecyclerViewAdapter.ViewHolder holder) {
            if (mUserListPresenter != null && userViewModel != null && actionMode == null)
                mUserListPresenter.onItemClicked((UserViewModel) userViewModel.getData(), (UserViewHolder) holder);
            else toggleSelection(position);
        }

        @Override
        public boolean onItemLongClicked(int position) {
            if (mUsersAdapter.isAllowSelection()) {
                actionMode = startSupportActionMode(UserListActivity.this);
                toggleSelection(position);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return new Intent(context, UserListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        mUserListPresenter.resume();
        mCompositeSubscription.add(RxView.clicks(mAddFab)
                .subscribe(aVoid -> {
                    Pair<View, String> pair = null;
                    if (Utils.hasLollipop())
                        pair = new Pair<>(mAddFab, mAddFab.getTransitionName());
                    mSharedElements = new ArrayList<>();
                    mSharedElements.add(pair);
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
                        addFragment(R.id.detail_container, fragment, mSharedElements);
                    } else navigator.navigateToUserDetails(this, -1, null);
                }));
    }

    @Override
    public void onPause() {
        super.onPause();
        mUserListPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mUserListPresenter.destroy();
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
        Utils.unsubscribeIfNotNull(mUsersAdapter.getCompositeSubscription());
    }

    private void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    private void initialize() {
        getComponent(UserComponent.class).inject(this);
        mUserListPresenter.setView(this);
        mCompositeSubscription.add(rxEventBus.toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultSubscriber<Object>() {
                               @Override
                               public void onCompleted() {
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                               }

                               @Override
                               public void onNext(Object event) {
                                   if (event instanceof String)
                                       Log.d(TAG, (String) event);
                                   if (event instanceof Integer)
                                       switch ((Integer) event) {
                                           case 100:
                                               loadUserList();
                                               return;
                                           default:
                                               break;
                                       }
                               }
                           }
                ));
    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) this).getComponent());
    }

    private void setupUI() {
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        onSearchRequested();
        Window window = getWindow();
        if (Utils.hasLollipop()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Utils.hasM())
                window.setStatusBarColor(getColor(R.color.title_color));
        }
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());
        if (findViewById(R.id.detail_container) != null) // Two pane for tablets(res/values-w900dp).
            mTwoPane = true;
        rv_users.setLayoutManager(new LinearLayoutManager(this));
        mUsersAdapter = new GenericRecyclerViewAdapter(getContext(), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case ItemInfo.HEADER: // header
                        return new RecyclerViewHeaderViewHolder(mLayoutInflater, parent);
                    case ItemInfo.FOOTER: // footer
                        return new RecyclerViewFooterViewHolder(mLayoutInflater, parent);
                    case ItemInfo.LOADING: // loading
                        return new RecyclerViewLoadingViewHolder(mLayoutInflater, parent);
                    default:
                        return new UserViewHolder(mLayoutInflater.inflate(viewType, parent, false));
                }
            }
        };
        mUsersAdapter.setOnItemClickListener(onItemClickListener);
        mUsersAdapter.setAllowSelection(true);
        rv_users.setAdapter(mUsersAdapter);
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
    public void renderItemList(List<UserViewModel> userViewModelCollection) {
        if (userViewModelCollection != null) {
            List<ItemInfo> mDataList = new ArrayList<>();
            for (UserViewModel userViewModel : userViewModelCollection)
                mDataList.add(new ItemInfo<UserViewModel>(userViewModel, R.layout.row_user) {
                    @Override
                    public long getId() {
                        return userViewModel.getUserId();
                    }
                });
            mUsersAdapter.setDataList(mDataList);
            mUsersAdapter.animateTo(mDataList);
            mUsersAdapter.setHasHeader(true, "Header!");
            mUsersAdapter.setHasFooter(true, "Footer!");
            rv_users.scrollToPosition(0);
        }
    }

    @Override
    public void viewItemDetail(UserViewModel userViewModel, UserViewHolder holder) {
        if (Utils.hasLollipop()) {
            Pair<View, String> firstPair = new Pair<>(holder.getAvatar(), holder.getAvatar()
                    .getTransitionName());
            Pair<View, String> secondPair = new Pair<>(holder.getTextViewTitle(),
                    holder.getTextViewTitle().getTransitionName());
            Pair<View, String> thirdPair = new Pair<>(mAddFab, mAddFab.getTransitionName());
            mSharedElements = new ArrayList<>();
            if (mTwoPane) {
                UserDetailsFragment fragment = new UserDetailsFragment();
                if (Utils.hasLollipop()) {
                    fragment.setSharedElementEnterTransition(new DetailsTransition());
                    fragment.setEnterTransition(new Fade());
                    fragment.setExitTransition(new Fade());
                    fragment.setSharedElementReturnTransition(new DetailsTransition());
                    mSharedElements.add(firstPair);
                    mSharedElements.add(secondPair);
                    mSharedElements.add(thirdPair);
                }
                Bundle arguments = new Bundle();
                arguments.putInt(UserDetailsFragment.ARG_ITEM_ID, userViewModel.getUserId());
                arguments.putString(UserDetailsFragment.ARG_ITEM_IMAGE, userViewModel.getCoverUrl());
                arguments.putString(UserDetailsFragment.ARG_ITEM_NAME, userViewModel.getFullName());
                fragment.setArguments(arguments);
                addFragment(R.id.detail_container, fragment, mSharedElements);
            } else
                navigator.navigateToUserDetails(this, userViewModel.getUserId(),
                        ActivityOptions.makeSceneTransitionAnimation(this, firstPair, secondPair, thirdPair).toBundle());
        } else
            navigator.navigateToUserDetails(this, userViewModel.getUserId(), null);
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
        mUserListPresenter.initialize();
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        loadUserList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        RxSearchView.queryTextChanges(mSearchView)
//                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
//                .throttleLast(100, TimeUnit.MILLISECONDS)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .onBackpressureLatest()
//                .doOnNext(query -> mUserListPresenter.search(query.toString()))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .onErrorResumeNext(Observable.empty())
//                .subscribe(new DefaultSubscriber<Object>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//
//                    }
//                });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mUserListPresenter.search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())
                    mUserListPresenter.showItemsListInView(mUserListPresenter.getItemsViewModels());
                else
                    mUserListPresenter.search(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Toggle the selection state of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private boolean toggleSelection(int position) {
        try {
            if (mUsersAdapter.isAllowSelection()) {
                mUsersAdapter.toggleSelection(position);
                int count = mUsersAdapter.getSelectedItemCount();
                if (count == 0) {
                    actionMode.finish();
                } else {
                    actionMode.setTitle(String.valueOf(count));
                    actionMode.invalidate();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selected_list_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.delete_item).setVisible(true).setEnabled(true);
        mToolbar.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                mUserListPresenter.deleteCollection(mUsersAdapter.getSelectedItemsIds());
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        try {
            mUsersAdapter.clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionMode = null;
        mToolbar.setVisibility(View.VISIBLE);
    }
}