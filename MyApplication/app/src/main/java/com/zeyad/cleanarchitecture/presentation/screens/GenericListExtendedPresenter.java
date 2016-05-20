package com.zeyad.cleanarchitecture.presentation.screens;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;

import java.util.List;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericListExtendedPresenter<M, H extends RecyclerView.ViewHolder> extends GenericListPresenter<M, H> {

    private List<M> mItemViewModels;

    public GenericListExtendedPresenter(GenericUseCase genericUseCase) {
        super(genericUseCase);
    }

    public abstract void search(String query);

    public abstract void deleteCollection(List<Long> ids);

    public List<M> getItemsViewModels() {
        return mItemViewModels;
    }

    // TODO: 10/05/16 combine Search and List subscribers!
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
        public void onNext(List<M> models) {
            mItemViewModels = models;
            showItemsListInView(models);
        }
    }

    public final class SearchSubscriber extends DefaultSubscriber<List<M>> {
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
        public void onNext(List<M> response) {
            showItemsListInView(response);
        }
    }

    public final class DeleteSubscriber extends DefaultSubscriber<Boolean> {
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
                getItemList();
//                mUsersAdapter.removeItems(mUsersAdapter.getSelectedItems());
                Log.d("OnDelete", "Success!");
            } else Log.d("OnDelete", "Fail!");
        }
    }
}