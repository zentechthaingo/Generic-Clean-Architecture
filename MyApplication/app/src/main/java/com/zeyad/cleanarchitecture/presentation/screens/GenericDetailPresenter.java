package com.zeyad.cleanarchitecture.presentation.screens;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;

/**
 * @author by zeyad on 17/05/16.
 */
public abstract class GenericDetailPresenter<M> implements BasePresenter {

    /**
     * id used to retrieve user details
     */
    private int mItemId;
    private GenericDetailView<M> mViewDetailsView;
    public final GenericUseCase mGetDetailsUseCase;

    public GenericDetailPresenter(GenericUseCase genericUseCase) {
        mGetDetailsUseCase = genericUseCase;
    }

    public void setView(@NonNull GenericDetailView<M> view) {
        mViewDetailsView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        mGetDetailsUseCase.unsubscribe();
    }

    /**
     * Initializes the presenter by start retrieving user details.
     */
    public void initialize(int itemId) {
        mItemId = itemId;
        loadOrderDetails();
    }

    /**
     * Loads user details.
     */
    private void loadOrderDetails() {
        hideViewRetry();
        showViewLoading();
        getItemDetails();
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

    private void showUserDetailsInView(M m) {
        mViewDetailsView.renderItem(m);
    }

    public abstract void getItemDetails();

    public final class ItemDetailSubscriber extends DefaultSubscriber<M> {
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
        public void onNext(M m) {
            showUserDetailsInView(m);
        }
    }
}