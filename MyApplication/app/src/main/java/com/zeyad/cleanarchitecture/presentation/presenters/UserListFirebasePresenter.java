package com.zeyad.cleanarchitecture.presentation.presenters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.views.FirebaseUserListView;
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;
import com.zeyad.cleanarchitecture.presentation.views.activities.UserListActivity;

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
                        ((UserListActivity) context).viewUser(userModel);
                });
            }
        });
    }
}