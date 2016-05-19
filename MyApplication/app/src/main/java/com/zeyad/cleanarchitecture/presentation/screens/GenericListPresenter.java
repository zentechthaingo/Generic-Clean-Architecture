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
public abstract class GenericListPresenter<M, H extends RecyclerView.ViewHolder> implements BasePresenter {

    private GenericListView<M, H> genericListView;
    public GenericUseCase getGeneralListUseCase;


    public GenericListPresenter(GenericUseCase genericUseCase) {
        getGeneralListUseCase = genericUseCase;
    }

    public void setView(@NonNull GenericListView<M, H> view) {
        genericListView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        getGeneralListUseCase.unsubscribe();
    }

    /**
     * Initializes the presenter by start retrieving the user list.
     */
    public void initialize() {
        loadItemList();
    }

    public void onItemClicked(M OrderHistoryViewModel, H holder) {
        genericListView.viewItemDetail(OrderHistoryViewModel, holder);
    }

    /**
     * Loads all users.
     */
    private void loadItemList() {
        hideViewRetry();
        showViewLoading();
        getItemList();
    }

    private void showViewLoading() {
        genericListView.showLoading();
    }

    private void hideViewLoading() {
        genericListView.hideLoading();
    }

    private void showViewRetry() {
        genericListView.showRetry();
    }

    private void hideViewRetry() {
        genericListView.hideRetry();
    }

    private void showErrorMessage(ErrorBundle errorBundle) {
        genericListView.showError(ErrorMessageFactory.create(genericListView.getContext(),
                errorBundle.getException()));
    }

    public void showItemsListInView(List<M> userViewModels) {
        genericListView.renderItemList(userViewModels);
    }

    public abstract void getItemList();

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