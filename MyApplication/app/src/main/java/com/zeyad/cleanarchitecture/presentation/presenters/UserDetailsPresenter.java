package com.zeyad.cleanarchitecture.presentation.presenters;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.BaseUseCase;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.model.mapper.UserModelDataMapper;
import com.zeyad.cleanarchitecture.presentation.views.UserDetailsView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * {@link BasePresenter} that controls communication between views and models of the presentation
 * layer.
 */
@PerActivity
public class UserDetailsPresenter implements BasePresenter {

    /**
     * id used to retrieve user details
     */
    private int userId;

    private UserDetailsView viewDetailsView;

    private final BaseUseCase getUserDetailsBaseUseCase;
    private final UserModelDataMapper userModelDataMapper;

    @Inject
    public UserDetailsPresenter(@Named("userDetails") BaseUseCase getUserDetailsBaseUseCase,
                                UserModelDataMapper userModelDataMapper) {
        this.getUserDetailsBaseUseCase = getUserDetailsBaseUseCase;
        this.userModelDataMapper = userModelDataMapper;
    }

    public void setView(@NonNull UserDetailsView view) {
        this.viewDetailsView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        getUserDetailsBaseUseCase.unsubscribe();
    }

    /**
     * Initializes the presenter by start retrieving user details.
     */
    public void initialize(int userId) {
        this.userId = userId;
        loadUserDetails();
    }

    /**
     * Loads user details.
     */
    private void loadUserDetails() {
        hideViewRetry();
        showViewLoading();
        getUserDetails();
    }

    private void showViewLoading() {
        viewDetailsView.showLoading();
    }

    private void hideViewLoading() {
        viewDetailsView.hideLoading();
    }

    private void showViewRetry() {
        viewDetailsView.showRetry();
    }

    private void hideViewRetry() {
        viewDetailsView.hideRetry();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(viewDetailsView.getContext(),
                errorBundle.getException());
        viewDetailsView.showError(errorMessage);
    }

    private void showUserDetailsInView(User user) {
        viewDetailsView.renderUser(userModelDataMapper.transform(user));
    }

    private void getUserDetails() {
        getUserDetailsBaseUseCase.execute(new UserDetailsSubscriber());
    }

    private final class UserDetailsSubscriber extends DefaultSubscriber<User> {

        @Override
        public void onCompleted() {
            hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(new DefaultErrorBundle((Exception) e));
            showViewRetry();
            e.printStackTrace();
        }

        @Override
        public void onNext(User user) {
            showUserDetailsInView(user);
        }
    }
}