package com.zeyad.cleanarchetecturet.presentation.presenter;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchetecturet.domain.User;
import com.zeyad.cleanarchetecturet.domain.exception.DefaultErrorBundle;
import com.zeyad.cleanarchetecturet.domain.exception.ErrorBundle;
import com.zeyad.cleanarchetecturet.domain.interactor.DefaultSubscriber;
import com.zeyad.cleanarchetecturet.domain.interactor.UseCase;
import com.zeyad.cleanarchetecturet.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchetecturet.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchetecturet.presentation.mapper.UserModelDataMapper;
import com.zeyad.cleanarchetecturet.presentation.model.UserModel;
import com.zeyad.cleanarchetecturet.presentation.view.UserDetailsView;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * {@link Presenter} that controls communication between views and models of the presentation
 * layer.
 */
@PerActivity
public class UserDetailsPresenter implements Presenter {

    /**
     * id used to retrieve user details
     */
    private int userId;

    private UserDetailsView viewDetailsView;

    private final UseCase getUserDetailsUseCase;
    private final UserModelDataMapper userModelDataMapper;

    @Inject
    public UserDetailsPresenter(@Named("userDetails") UseCase getUserDetailsUseCase,
                                UserModelDataMapper userModelDataMapper) {
        this.getUserDetailsUseCase = getUserDetailsUseCase;
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
        getUserDetailsUseCase.unsubscribe();
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
        final UserModel userModel = userModelDataMapper.transform(user);
        viewDetailsView.renderUser(userModel);
    }

    private void getUserDetails() {
        getUserDetailsUseCase.execute(new UserDetailsSubscriber());
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
        }

        @Override
        public void onNext(User user) {
            showUserDetailsInView(user);
        }
    }
}