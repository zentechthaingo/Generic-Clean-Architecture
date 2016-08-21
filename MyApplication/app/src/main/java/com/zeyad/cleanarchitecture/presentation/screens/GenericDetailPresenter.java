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
public abstract class GenericDetailPresenter<M> extends BasePresenter {

    /**
     * id used to retrieve item details
     */
    public int mItemId;
    private GenericDetailView<M> mGenericDetailView;

    public GenericDetailPresenter(GenericUseCase genericUseCase) {
        super(genericUseCase);
    }

    public void setView(@NonNull GenericDetailView<M> view) {
        mGenericDetailView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    /**
     * Initializes the presenter by start retrieving item details.
     */
    public void initialize(int itemId) {
        mItemId = itemId;
        loadItemDetails();
    }

    /**
     * Loads item details.
     */
    private void loadItemDetails() {
        hideViewRetry();
        showViewLoading();
        getItemDetails();
    }

    public void showViewLoading() {
        mGenericDetailView.showLoading();
    }

    public void hideViewLoading() {
        mGenericDetailView.hideLoading();
    }

    public void showViewRetry() {
        mGenericDetailView.showRetry();
    }

    public void hideViewRetry() {
        mGenericDetailView.hideRetry();
    }

    public void showErrorMessage(ErrorBundle errorBundle) {
        mGenericDetailView.showError(ErrorMessageFactory.create(mGenericDetailView.getApplicationContext(),
                errorBundle.getException()));
    }

    private void showUserDetailsInView(M m) {
        mGenericDetailView.renderItem(m);
    }

    public abstract void getItemDetails();

    public int getItemId() {
        return mItemId;
    }

    public GenericDetailView<M> getGenericDetailView() {
        return mGenericDetailView;
    }

    public void setItemId(int mItemId) {
        this.mItemId = mItemId;
    }

    public void setViewDetailsView(GenericDetailView<M> mViewDetailsView) {
        this.mGenericDetailView = mViewDetailsView;
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
            showUserDetailsInView(m);
        }
    }
}