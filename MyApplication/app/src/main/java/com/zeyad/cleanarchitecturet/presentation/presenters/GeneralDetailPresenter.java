package com.zeyad.cleanarchitecturet.presentation.presenters;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecturet.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecturet.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecturet.domain.interactor.DefaultSubscriber;
import com.zeyad.cleanarchitecturet.domain.interactor.GeneralizedUseCase;
import com.zeyad.cleanarchitecturet.domain.models.User;
import com.zeyad.cleanarchitecturet.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchitecturet.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecturet.presentation.model.UserModel;
import com.zeyad.cleanarchitecturet.presentation.views.UserDetailsView;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class GeneralDetailPresenter implements BasePresenter {

    /**
     * id used to retrieve user details
     */
    private int userId;
    private UserDetailsView viewDetailsView;
    private final GeneralizedUseCase getUserDetailsBaseUseCase;

    @Inject
    public GeneralDetailPresenter(@Named("generalEntityDetail") GeneralizedUseCase getUserDetailsBaseUseCase) {
        this.getUserDetailsBaseUseCase = getUserDetailsBaseUseCase;
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
        viewDetailsView.showError(ErrorMessageFactory.create(viewDetailsView.getContext(),
                errorBundle.getException()));
    }

    private void showUserDetailsInView(UserModel userModel) {
        viewDetailsView.renderUser(userModel);
    }

    private void getUserDetails() {
        getUserDetailsBaseUseCase.execute(new UserDetailsSubscriber(), UserModel.class, User.class,
                UserRealmModel.class, userId);
    }

    private final class UserDetailsSubscriber extends DefaultSubscriber<UserModel> {
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
        public void onNext(UserModel userModel) {
            showUserDetailsInView(userModel);
        }
    }
}