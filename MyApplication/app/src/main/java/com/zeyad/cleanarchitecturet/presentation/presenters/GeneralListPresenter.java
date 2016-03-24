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
import com.zeyad.cleanarchitecturet.presentation.views.UserListView;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class GeneralListPresenter implements BasePresenter {

    private UserListView viewListView;
    private GeneralizedUseCase getGeneralListUseCase;

    @Inject
    public GeneralListPresenter(@Named("generalEntityList") GeneralizedUseCase getUserListUserCase) {
        getGeneralListUseCase = getUserListUserCase;
    }

    public void setView(@NonNull UserListView view) {
        viewListView = view;
    }

    @Override
    public void resume() {
//        getUserList();
    }

    @Override
    public void pause() {
        getGeneralListUseCase.unsubscribe();
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

    public void onUserClicked(UserModel userModel) {
        viewListView.viewUser(userModel);
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

    private void showUsersCollectionInView(Collection<UserModel> userModels) {
        viewListView.renderUserList(userModels);
    }

    private void getUserList() {
        getGeneralListUseCase.execute(new UserListSubscriber(), UserModel.class, User.class, UserRealmModel.class);
    }

    private final class UserListSubscriber extends DefaultSubscriber<List<UserModel>> {
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
        public void onNext(List<UserModel> users) {
            showUsersCollectionInView(users);
        }
    }
}