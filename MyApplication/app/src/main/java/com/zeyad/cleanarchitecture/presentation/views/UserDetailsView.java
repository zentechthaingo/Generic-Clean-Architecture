package com.zeyad.cleanarchitecture.presentation.views;

import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

/**
 * Interface representing a View in a model view presenter (MVP) pattern.
 * In this case is used as a view representing a user profile.
 */
public interface UserDetailsView extends LoadDataView {
    /**
     * Render a user in the UI.
     *
     * @param userViewModel The {@link UserViewModel} that will be shown.
     */
    void renderUser(UserViewModel userViewModel);

    /**
     * Show user edit form
     */
    void editUser(UserViewModel userViewModel);

    /**
     * Submit a user to be edited.
     */
    void putUserSuccess(UserViewModel userViewModel);

    /**
     * Retrieves the validated user to be submitted
     *
     * @return {@link UserViewModel}
     */
    UserViewModel getValidatedUser();
}