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
    public int mItemId;
    public M mItemViewModel;
    public GenericDetailView<M> mViewDetailsView;
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

    public int getmItemId() {
        return mItemId;
    }

    public void setmItemId(int mItemId) {
        this.mItemId = mItemId;
    }

    public abstract void getItemDetails();

    /**
     * Loads user details.
     */
    private void loadOrderDetails() {
        hideViewRetry();
        showViewLoading();
        getItemDetails();
    }

    public void showViewLoading() {
        mViewDetailsView.showLoading();
    }

    public void hideViewLoading() {
        mViewDetailsView.hideLoading();
    }

    public void showViewRetry() {
        mViewDetailsView.showRetry();
    }

    private void hideViewRetry() {
        mViewDetailsView.hideRetry();
    }

    public void showErrorMessage(ErrorBundle errorBundle) {
        mViewDetailsView.showError(ErrorMessageFactory.create(mViewDetailsView.getContext(),
                errorBundle.getException()));
    }

    private void showItemDetailsInView(M m) {
        mItemViewModel = m;
        mViewDetailsView.renderItem(m);
    }

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
            showItemDetailsInView(m);
        }
    }
}