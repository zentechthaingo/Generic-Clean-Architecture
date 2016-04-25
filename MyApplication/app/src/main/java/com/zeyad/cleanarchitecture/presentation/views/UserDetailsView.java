package com.zeyad.cleanarchitecture.presentation.views;

import com.zeyad.cleanarchitecture.presentation.model.UserModel;

/**
 * Interface representing a View in a model view presenter (MVP) pattern.
 * In this case is used as a view representing a user profile.
 */
public interface UserDetailsView extends LoadDataView {
    /**
     * Render a user in the UI.
     *
     * @param userModel The {@link UserModel} that will be shown.
     */
    void renderUser(UserModel userModel);

    /**
     * Show user edit form
     */
    void editUser(UserModel userModel);

    /**
     * Submit a user to be edited.
     */
    void putUserSuccess(UserModel userModel);

    /**
     * Retrieves the validated user to be submitted
     *
     * @return {@link UserModel}
     */
    UserModel getValidatedUser();
}