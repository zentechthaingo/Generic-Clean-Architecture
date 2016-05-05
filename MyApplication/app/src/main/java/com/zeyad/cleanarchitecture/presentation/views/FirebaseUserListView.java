package com.zeyad.cleanarchitecture.presentation.views;

import com.firebase.ui.FirebaseRecyclerAdapter;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

/**
 * Interface representing a View in a model view presenter (MVP) pattern.
 * In this case is used as a view representing a list of {@link UserViewModel}.
 */
public interface FirebaseUserListView extends UserListView {
    /**
     * Adapter a {@link UserViewModel} profile/details.
     *
     * @param firebaseRecyclerAdapter The user that will be shown.
     */
    void setupFirebaseAdapter(FirebaseRecyclerAdapter<UserViewModel, UserViewHolder> firebaseRecyclerAdapter);

    /**
     * Render a user list in the UI.
     *
     * @param firebaseRecyclerAdapter The collection of {@link UserViewModel} that will be shown.
     */
    void renderUserList(FirebaseRecyclerAdapter<UserViewModel, UserViewHolder> firebaseRecyclerAdapter);
}