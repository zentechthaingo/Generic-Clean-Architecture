package com.zeyad.cleanarchitecturet.presentation.presenters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.zeyad.cleanarchitecturet.R;
import com.zeyad.cleanarchitecturet.domain.User;
import com.zeyad.cleanarchitecturet.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecturet.presentation.model.UserModel;
import com.zeyad.cleanarchitecturet.presentation.view.FirebaseUserListView;
import com.zeyad.cleanarchitecturet.presentation.view.UserViewHolder;
import com.zeyad.cleanarchitecturet.presentation.view.activities.UserListActivity;

import javax.inject.Inject;

@PerActivity
public class UserListFirebasePresenter implements BasePresenter {

    RecyclerView rv_users;
    private Firebase ref;
    private Activity context;
    private FirebaseUserListView viewListView;

    @Inject
    public UserListFirebasePresenter(Firebase ref, Context context) {
        this.ref = ref;
        this.context = (Activity) context;
    }

    public void setView(@NonNull FirebaseUserListView view, RecyclerView rv_users) {
        viewListView = view;
        this.rv_users = rv_users;
    }

    @Override
    public void resume() {
        getUserList();
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
    }

    /**
     * Initializes the presenter by start retrieving the user list.
     */
    public void initialize() {
        loadUserList();
    }

    /**
     * Adds User to list
     *
     * @param user
     */
    public void addItem(User user) {
        ref.push().setValue(user);
    }

    /**
     * Loads all users.
     */
    private void loadUserList() {
        hideViewRetry();
        showViewLoading();
        getUserList();
    }

    private void showViewLoading() {
        viewListView.showLoading();
    }

    private void hideViewLoading() {
        viewListView.hideLoading();
    }

    private void hideViewRetry() {
        viewListView.hideRetry();
    }

    private void getUserList() {
        hideViewLoading();
        viewListView.setupFirebaseAdapter(new FirebaseRecyclerAdapter<UserModel, UserViewHolder>(UserModel.class,
                R.layout.row_user, UserViewHolder.class, ref) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, final UserModel userModel, int i) {
                userViewHolder.getTextViewTitle().setText(userModel.getFullName());
                userViewHolder.itemView.setOnClickListener(v -> {
                    if (context instanceof UserListActivity)
                        ((UserListActivity) context).onUserClicked(userModel);
                });
            }
        });
    }
}