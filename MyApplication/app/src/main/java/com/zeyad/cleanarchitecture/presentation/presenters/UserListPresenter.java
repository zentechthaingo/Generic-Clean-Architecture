package com.zeyad.cleanarchitecture.presentation.presenters;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.BaseUseCase;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.presentation.view_models.mapper.UserViewModelDataMapper;
import com.zeyad.cleanarchitecture.presentation.views.UserListView;
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * {@link BasePresenter} that controls communication between views and models of the presentation
 * layer.
 */
@PerActivity
public class UserListPresenter implements BasePresenter {

    private UserListView viewListView;
    private final BaseUseCase getUserListBaseUseCase;
    private final UserViewModelDataMapper userViewModelDataMapper;

    @Inject
    public UserListPresenter(@Named("userEntityList") BaseUseCase getUserListUserCase, UserViewModelDataMapper userViewModelDataMapper) {
        getUserListBaseUseCase = getUserListUserCase;
        this.userViewModelDataMapper = userViewModelDataMapper;
    }

    public void setView(@NonNull UserListView view) {
        viewListView = view;
    }

    @Override
    public void resume() {
        getUserList();
    }

    @Override
    public void pause() {
        getUserListBaseUseCase.unsubscribe();
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
     * Loads all users.
     */
    private void loadUserList() {
        hideViewRetry();
        showViewLoading();
        getUserList();
    }

    public void onUserClicked(UserViewModel userViewModel, UserViewHolder holder) {
        viewListView.viewUser(userViewModel, holder);
    }

    private void showViewLoading() {
        viewListView.showLoading();
    }

    private void hideViewLoading() {
        viewListView.hideLoading();
    }

    private void showViewRetry() {
        viewListView.showRetry();
    }

    private void hideViewRetry() {
        viewListView.hideRetry();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        viewListView.showError(ErrorMessageFactory.create(viewListView.getContext(),
                errorBundle.getException()));
    }

    private void showUsersCollectionInView(List<User> usersCollection) {
        viewListView.renderUserList(userViewModelDataMapper.transform(usersCollection));
    }

    private void getUserList() {
        getUserListBaseUseCase.execute(new UserListSubscriber());
    }

    private final class UserListSubscriber extends DefaultSubscriber<List<User>> {
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
        public void onNext(List<User> users) {
            showUsersCollectionInView(users);
        }
    }
}