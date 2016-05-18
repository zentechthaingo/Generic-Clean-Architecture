package com.zeyad.cleanarchitecture.presentation.screens.users.list;

import android.support.annotation.NonNull;
import android.util.Log;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.screens.BasePresenter;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserListView;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserViewHolder;
import com.zeyad.cleanarchitecture.utilities.Constants;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

@PerActivity
public class GenericListPresenter implements BasePresenter {

    private UserListView viewListView;
    private GenericUseCase getGeneralListUseCase;
    private List<UserViewModel> mUserViewModels;

    @Inject
    public GenericListPresenter(GenericUseCase getUserListUserCase) {
        getGeneralListUseCase = getUserListUserCase;
    }

    public void setView(@NonNull UserListView view) {
        viewListView = view;
    }

    @Override
    public void resume() {
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

    public void onUserClicked(UserViewModel userViewModel, UserViewHolder holder) {
        viewListView.viewUser(userViewModel, holder);
    }

//    public void search(SearchView searchView) {
//        RxSearchView.queryTextChanges(searchView)
//                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
//                .throttleLast(100, TimeUnit.MILLISECONDS)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .onBackpressureLatest()
//                .flatMap(query -> getGeneralListUseCase.executeSearch(query.toString(),
//                        UserRealmModel.FULL_NAME_COLUMN, UserViewModel.class, User.class, UserRealmModel.class))
////                        .distinct()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .onErrorResumeNext(Observable.empty())
//                .subscribe(new SearchSubscriber());
//    }

    public void search(String query) {
        getGeneralListUseCase.executeSearch(query, UserRealmModel.FULL_NAME_COLUMN, new SearchSubscriber(),
                UserViewModel.class, User.class, UserRealmModel.class);
    }

    public void deleteCollection(List<Integer> ids) {
        HashMap<String, Object> keyValuePairs = new HashMap<>(1);
        keyValuePairs.put("ids", ids);
        getGeneralListUseCase.executeDeleteCollection(new DeleteSubscriber(), "", keyValuePairs,
                User.class, UserRealmModel.class, true);
    }

    public List<UserViewModel> getUserModels() {
        return mUserViewModels;
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

    public void showUsersCollectionInView(List<UserViewModel> userViewModels) {
        viewListView.renderUserList(userViewModels);
    }

    private void getUserList() {
        getGeneralListUseCase.executeList(new UserListSubscriber(), Constants.API_BASE_URL + "users.json",
                UserViewModel.class, User.class, UserRealmModel.class, true);
    }

    // TODO: 10/05/16 combine Search and List subscribers!
    private final class UserListSubscriber extends DefaultSubscriber<List<UserViewModel>> {
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
        public void onNext(List<UserViewModel> users) {
            mUserViewModels = users;
            showUsersCollectionInView(users);
        }
    }

    private final class SearchSubscriber extends DefaultSubscriber<List<UserViewModel>> {
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
        public void onNext(List<UserViewModel> response) {
            showUsersCollectionInView(response);
        }
    }

    private final class DeleteSubscriber extends DefaultSubscriber<Boolean> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(new DefaultErrorBundle((Exception) e));
            showViewRetry();
            e.printStackTrace();
        }

        // TODO: 4/17/16 Apply adapter method!
        @Override
        public void onNext(Boolean success) {
            if (success) {
                getUserList();
//                mUsersAdapter.removeItems(mUsersAdapter.getmSelectedItems());
                Log.d("OnDelete", "Success!");
            } else Log.d("OnDelete", "Fail!");
        }
    }
}