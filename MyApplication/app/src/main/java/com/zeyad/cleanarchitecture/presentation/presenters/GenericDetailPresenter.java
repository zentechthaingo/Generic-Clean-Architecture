package com.zeyad.cleanarchitecture.presentation.presenters;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.presentation.views.UserDetailsView;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class GenericDetailPresenter implements BasePresenter {

    /**
     * id used to retrieve user details
     */
    private int mUserId;
    private UserViewModel mUserViewModel;
    private UserDetailsView mViewDetailsView;
    private final GenericUseCase mGetUserDetailsBaseUseCase;

    @Inject
    public GenericDetailPresenter(@Named("generalizedDetailUseCase") GenericUseCase genericUseCase) {
        mGetUserDetailsBaseUseCase = genericUseCase;
    }

    public void setView(@NonNull UserDetailsView view) {
        this.mViewDetailsView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
        mGetUserDetailsBaseUseCase.unsubscribe();
    }

    @Override
    public void destroy() {
    }

    /**
     * Initializes the presenter by start retrieving user details.
     */
    public void initialize(int userId) {
        mUserId = userId;
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
        mViewDetailsView.showLoading();
    }

    private void hideViewLoading() {
        mViewDetailsView.hideLoading();
    }

    private void showViewRetry() {
        mViewDetailsView.showRetry();
    }

    private void hideViewRetry() {
        mViewDetailsView.hideRetry();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        mViewDetailsView.showError(ErrorMessageFactory.create(mViewDetailsView.getContext(),
                errorBundle.getException()));
    }

    private void showUserDetailsInView(UserViewModel userViewModel) {
        mUserViewModel = userViewModel;
        mViewDetailsView.renderUser(this.mUserViewModel);
    }

    private void showUserPutSuccess(@NonNull UserViewModel userViewModel) {
        hideViewLoading();
        mUserViewModel = userViewModel;
        mViewDetailsView.putUserSuccess(mUserViewModel);
    }

    private void getUserDetails() {
        mGetUserDetailsBaseUseCase.executeDetail(new UserDetailsSubscriber(), UserViewModel.class,
                User.class, UserRealmModel.class, mUserId);
    }

    public void setupEdit() {
        mViewDetailsView.editUser(mUserViewModel);
    }

    public void submitEdit() {
        showViewLoading();
        mGetUserDetailsBaseUseCase.executePut(new PutSubscriber(), mViewDetailsView.getValidatedUser(),
                UserViewModel.class, User.class, UserRealmModel.class);
    }

    private final class UserDetailsSubscriber extends DefaultSubscriber<UserViewModel> {
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
        public void onNext(UserViewModel userViewModel) {
            showUserDetailsInView(userViewModel);
        }
    }

    private final class PutSubscriber extends DefaultSubscriber<UserViewModel> {
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
        public void onNext(UserViewModel userViewModel) {
            showUserPutSuccess(userViewModel);
        }
    }
}