package com.zeyad.cleanarchitecturet.presentation.views;

import com.firebase.ui.FirebaseRecyclerAdapter;
import com.zeyad.cleanarchitecturet.presentation.model.UserModel;

/**
 * Interface representing a View in a model view presenter (MVP) pattern.
 * In this case is used as a view representing a list of {@link UserModel}.
 */
public interface FirebaseUserListView extends UserListView {
    /**
     * Adapter a {@link UserModel} profile/details.
     *
     * @param firebaseRecyclerAdapter The user that will be shown.
     */
    void setupFirebaseAdapter(FirebaseRecyclerAdapter<UserModel, UserViewHolder> firebaseRecyclerAdapter);

    /**
     * Render a user list in the UI.
     *
     * @param firebaseRecyclerAdapter The collection of {@link UserModel} that will be shown.
     */
    void renderUserList(FirebaseRecyclerAdapter<UserModel, UserViewHolder> firebaseRecyclerAdapter);
}