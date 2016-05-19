package com.zeyad.cleanarchitecture.presentation.screens;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericExtendedDetailPresenter<M> extends GenericDetailPresenter<M> {

    public GenericEditableItemView<M> mViewDetailsView;

    public GenericExtendedDetailPresenter(GenericUseCase genericUseCase) {
        super(genericUseCase);
    }

    public void setView(@NonNull GenericEditableItemView<M> view) {
        mViewDetailsView = view;
    }

    private void showItemPutSuccess(@NonNull M model) {
        hideViewLoading();
        mItemViewModel = model;
        mViewDetailsView.putItemSuccess(mItemViewModel);
    }

    public void setupEdit() {
        mViewDetailsView.editItem(mItemViewModel);
    }

    public abstract void submitEdit();

    public final class PutSubscriber extends DefaultSubscriber<M> {
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
        public void onNext(M model) {
            showItemPutSuccess(model);
        }
    }
}