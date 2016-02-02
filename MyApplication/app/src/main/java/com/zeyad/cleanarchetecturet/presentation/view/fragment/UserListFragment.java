package com.zeyad.cleanarchetecturet.presentation.view.fragment;

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

import com.zeyad.cleanarchetecturet.R;
import com.zeyad.cleanarchetecturet.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchetecturet.presentation.model.UserModel;
import com.zeyad.cleanarchetecturet.presentation.presenter.UserListPresenter;
import com.zeyad.cleanarchetecturet.presentation.view.UserListView;
import com.zeyad.cleanarchetecturet.presentation.view.adapter.UsersAdapter;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment that shows a list of Users.
 */
public class UserListFragment extends BaseFragment implements UserListView {

    /**
     * Interface for listening user list events.
     */
    public interface UserListListener {
        void onUserClicked(final UserModel userModel);
    }

    @Inject
    UserListPresenter userListPresenter;
    @Bind(R.id.rv_users)
    RecyclerView rv_users;
    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;
    @Bind(R.id.rl_retry)
    RelativeLayout rl_retry;
    @Bind(R.id.bt_retry)
    Button bt_retry;
    private UsersAdapter usersAdapter;
    private UserListListener userListListener;

    public UserListFragment() {
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
        userListPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        userListPresenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userListPresenter.destroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initialize() {
        getComponent(UserComponent.class).inject(this);
        userListPresenter.setView(this);
    }

    private void setupUI() {
        rv_users.setLayoutManager(new LinearLayoutManager(getActivity()));
        usersAdapter = new UsersAdapter(getActivity(), new ArrayList<UserModel>());
        usersAdapter.setOnItemClickListener(onItemClickListener);
        rv_users.setAdapter(usersAdapter);
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
    public void renderUserList(Collection<UserModel> userModelCollection) {
        if (userModelCollection != null)
            usersAdapter.setUsersCollection(userModelCollection);
    }

    @Override
    public void viewUser(UserModel userModel) {
        if (userListListener != null)
            userListListener.onUserClicked(userModel);
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
        userListPresenter.initialize();
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        loadUserList();
    }

    private UsersAdapter.OnItemClickListener onItemClickListener =
            new UsersAdapter.OnItemClickListener() {
                @Override
                public void onUserItemClicked(UserModel userModel) {
                    if (userListPresenter != null && userModel != null)
                        userListPresenter.onUserClicked(userModel);
                }
            };
}
