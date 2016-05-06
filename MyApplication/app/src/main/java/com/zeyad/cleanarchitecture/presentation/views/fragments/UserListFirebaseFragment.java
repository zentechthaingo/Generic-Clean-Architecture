package com.zeyad.cleanarchitecture.presentation.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.firebase.ui.FirebaseRecyclerAdapter;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.presentation.views.FirebaseUserListView;
import com.zeyad.cleanarchitecture.presentation.presenters.UserListFirebasePresenter;
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment that shows a list of Users.
 */
public class UserListFirebaseFragment extends BaseFragment implements FirebaseUserListView {

    /**
     * Interface for listening user list events.
     */
    public interface UserListListener {
        void onUserClicked(final UserViewModel userViewModel);
    }

    @Inject
    UserListFirebasePresenter userListFirebasePresenter;
    @Bind(R.id.rv_users)
    RecyclerView rv_users;
    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;
    @Bind(R.id.rl_retry)
    RelativeLayout rl_retry;
    @Bind(R.id.bt_retry)
    Button bt_retry;
    private FirebaseRecyclerAdapter<UserViewModel, UserViewHolder> recyclerAdapter;
    private UserListListener userListListener;

    public UserListFirebaseFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof UserListListener)
            userListListener = (UserListListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_user_list, container, true);
        ButterKnife.bind(this, fragmentView);
        setupUI();
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        loadUserList();
    }

    @Override
    public void onResume() {
        super.onResume();
        userListFirebasePresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        userListFirebasePresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerAdapter.cleanup();
        userListFirebasePresenter.destroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initialize() {
        getComponent(UserComponent.class).inject(this);
        userListFirebasePresenter.setView(this, rv_users);
    }

    // TODO: 2/28/16 setup adapter!
    private void setupUI() {
        rv_users.setHasFixedSize(true);
        rv_users.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void showLoading() {
        rl_progress.setVisibility(View.VISIBLE);
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        rl_progress.setVisibility(View.GONE);
        getActivity().setProgressBarIndeterminateVisibility(false);
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
    public void renderUserList(FirebaseRecyclerAdapter<UserViewModel, UserViewHolder> adapter) {
        recyclerAdapter = adapter;
        rv_users.setAdapter(recyclerAdapter);
    }

    @Override
    public void renderUserList(List<UserViewModel> userViewModelCollection) {
    }

    @Override
    public void viewUser(UserViewModel userViewModel, UserViewHolder holder) {
        if (userListListener != null)
            userListListener.onUserClicked(userViewModel);
    }

    @Override
    public void setupFirebaseAdapter(FirebaseRecyclerAdapter<UserViewModel, UserViewHolder> recyclerAdapter) {
        this.recyclerAdapter = recyclerAdapter;
        rv_users.setAdapter(recyclerAdapter);
    }

    @Override
    public void showError(String message) {
        showToastMessage(message);
    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    /**
     * Loads all users.
     */
    private void loadUserList() {
        userListFirebasePresenter.initialize();
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        loadUserList();
    }
}