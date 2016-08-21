package com.zeyad.cleanarchitecture.presentation.screens;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;

import java.util.List;

/**
 * @author by zeyad on 17/05/16.
 */
public abstract class GenericListPresenter<M, H extends RecyclerView.ViewHolder> extends BasePresenter {

    private GenericListView<M, H> mGenericListView;

    public GenericListPresenter(GenericUseCase genericUseCase) {
        super(genericUseCase);
    }

    public void setView(@NonNull GenericListView<M, H> view) {
        mGenericListView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    /**
     * Initializes the presenter by start retrieving the user list.
     */
    public void initialize() {
        loadItemList();
    }

    public void onItemClicked(M OrderHistoryViewModel, H holder) {
        mGenericListView.viewItemDetail(OrderHistoryViewModel, holder);
    }

    /**
     * Loads all users.
     */
    private void loadItemList() {
        hideViewRetry();
        showViewLoading();
        getItemList();
    }

    public void showViewLoading() {
        mGenericListView.showLoading();
    }

    public void hideViewLoading() {
        mGenericListView.hideLoading();
    }

    public void showViewRetry() {
        mGenericListView.showRetry();
    }

    public void hideViewRetry() {
        mGenericListView.hideRetry();
    }

    public void showErrorMessage(ErrorBundle errorBundle) {
        mGenericListView.showError(ErrorMessageFactory.create(mGenericListView.getApplicationContext(),
                errorBundle.getException()));
    }

    public void showItemsListInView(List<M> userViewModels) {
        mGenericListView.renderItemList(userViewModels);
    }

    public abstract void getItemList();

    public GenericListView<M, H> getGenericListView() {
        return mGenericListView;
    }

    public final class ItemListSubscriber extends DefaultSubscriber<List<M>> {
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
        public void onNext(List<M> mList) {
            showItemsListInView(mList);
        }
    }
}