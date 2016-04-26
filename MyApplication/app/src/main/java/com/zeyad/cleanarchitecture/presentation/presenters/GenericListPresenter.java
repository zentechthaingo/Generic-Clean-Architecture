package com.zeyad.cleanarchitecture.presentation.presenters;

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
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.views.UserListView;
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@PerActivity
public class GenericListPresenter implements BasePresenter {

    private UserListView viewListView;
    private GenericUseCase getGeneralListUseCase;
    private List<UserModel> mUserModels;

    @Inject
    public GenericListPresenter(@Named("generalizedUseCase") GenericUseCase getUserListUserCase) {
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

    public void onUserClicked(UserModel userModel, UserViewHolder holder) {
        viewListView.viewUser(userModel, holder);
    }

//    public void search(SearchView searchView) {
//        RxSearchView.queryTextChanges(searchView)
//                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
//                .throttleLast(100, TimeUnit.MILLISECONDS)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .onBackpressureLatest()
//                .flatMap(query -> getGeneralListUseCase.executeSearch(query.toString(),
//                        UserRealmModel.FULL_NAME_COLUMN, UserModel.class, User.class, UserRealmModel.class))
////                        .distinct()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .onErrorResumeNext(Observable.empty())
//                .subscribe(new SearchSubscriber());
//    }

    public void search(List<UserModel> userModels, String query) {
        mUserModels = userModels;
//        showUsersCollectionInView(filter(userModels, query));
        getGeneralListUseCase.executeSearch(query, "full_name", new SearchSubscriber(),
                UserModel.class, User.class, UserRealmModel.class);
    }

    public void deleteCollection(List<Integer> ids) {
        getGeneralListUseCase.executeDeleteCollection(new DeleteSubscriber(), ids, User.class, UserRealmModel.class);
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

    public void showUsersCollectionInView(Collection<UserModel> userModels) {
        viewListView.renderUserList(userModels);
    }

    private void getUserList() {
        getGeneralListUseCase.executeList(new UserListSubscriber(), UserModel.class, User.class,
                UserRealmModel.class);
    }

    private final class UserListSubscriber extends DefaultSubscriber<Collection<UserModel>> {
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
        public void onNext(Collection<UserModel> users) {
            showUsersCollectionInView(users);
        }
    }

    private final class SearchSubscriber extends DefaultSubscriber<Collection<UserModel>> {
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

        @Override
        public void onNext(Collection<UserModel> response) {
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

    public List<UserModel> getmUserModels() {
        return mUserModels;
    }
}