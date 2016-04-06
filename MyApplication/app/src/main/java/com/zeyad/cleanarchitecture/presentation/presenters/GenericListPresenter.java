package com.zeyad.cleanarchitecture.presentation.presenters;

import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactor.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactor.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.views.UserListView;
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@PerActivity
public class GenericListPresenter implements BasePresenter {

    private UserListView viewListView;
    private GenericUseCase getGeneralListUseCase;

    @Inject
    public GenericListPresenter(@Named("generalizedUseCase") GenericUseCase getUserListUserCase) {
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

    public void onUserClicked(UserModel userModel, UserViewHolder holder) {
        viewListView.viewUser(userModel, holder);
    }

    public Observable search(CharSequence charSequence) {
        return getGeneralListUseCase.executeSearch(charSequence, new UserListSubscriber(), UserModel.class,
                User.class, UserRealmModel.class);
    }

    public void search(SearchView searchView) {
        RxSearchView.queryTextChanges(searchView)
                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(200, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .flatMap(charSequence -> search(charSequence))
//                        .distinct()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onErrorResumeNext(Observable.empty())
                .subscribe(new SearchSubscriber());
    }

    /**
     * Loads all users.
     */
    private void loadUserList() {
        hideViewRetry();
        showViewLoading();
        getUserList();
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
        getGeneralListUseCase.executeList(new UserListSubscriber(), UserModel.class, User.class,
                UserRealmModel.class);
    }

    public void showSearchResult(Set<UserModel> response) {
        showUsersCollectionInView(response);
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

    private final class SearchSubscriber extends DefaultSubscriber<Set<UserModel>> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(Set<UserModel> response) {
            showSearchResult(response);
        }
    }
}