package com.zeyad.cleanarchitecture.presentation.views;

import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

import java.util.List;

/**
 * Interface representing a View in a model view presenter (MVP) pattern.
 * In this case is used as a view representing a list of {@link UserViewModel}.
 */
public interface UserListView extends LoadDataView {
    /**
     * Render a user list in the UI.
     *
     * @param userViewModelCollection The collection of {@link UserViewModel} that will be shown.
     */
    void renderUserList(List<UserViewModel> userViewModelCollection);

    /**
     * View a {@link UserViewModel} profile/details.
     *
     * @param userViewModel The user that will be shown.
     */
    void viewUser(UserViewModel userViewModel, UserViewHolder holder);
}