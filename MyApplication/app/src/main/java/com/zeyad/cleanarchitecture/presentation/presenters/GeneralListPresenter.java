package com.zeyad.cleanarchitecture.presentation.presenters;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactor.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactor.GeneralizedUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.views.UserListView;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class GeneralListPresenter implements BasePresenter {

    private UserListView viewListView;
    private GeneralizedUseCase getGeneralListUseCase;

    @Inject
    public GeneralListPresenter(@Named("generalizedUseCase") GeneralizedUseCase getUserListUserCase) {
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