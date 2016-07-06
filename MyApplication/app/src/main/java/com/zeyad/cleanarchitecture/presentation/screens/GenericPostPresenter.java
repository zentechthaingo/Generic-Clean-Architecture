package com.zeyad.cleanarchitecture.presentation.screens;

import android.support.annotation.NonNull;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;

import java.util.HashMap;

/**
 * @author by zeyad on 23/05/16.
 */
public abstract class GenericPostPresenter<M> extends BasePresenter {

    private GenericPostView<M> mGenericPostView;

    public GenericPostPresenter(GenericUseCase getCityListCityCase) {
        super(getCityListCityCase);
    }

    public void setView(@NonNull GenericPostView<M> view) {
        mGenericPostView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    public void showViewLoading() {
        mGenericPostView.showLoading();
    }

    public void hideViewLoading() {
        mGenericPostView.hideLoading();
    }

    public void showErrorMessage(ErrorBundle errorBundle) {
        mGenericPostView.showError(ErrorMessageFactory.create(mGenericPostView.getContext(),
                errorBundle.getException()));
    }

    /**
     * Call showViewLoading() then execute Post Call.
     *
     * @param postBundle data to be posted.
     */
    public abstract void post(HashMap<String, Object> postBundle);

    public void postSuccess(M model) {
        mGenericPostView.postSuccessful(model);
    }

    public GenericPostView<M> getGenericPostView() {
        return mGenericPostView;
    }

    public final class PostSubscriber extends DefaultSubscriber<M> {
        @Override
        public void onCompleted() {
            hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(new DefaultErrorBundle((Exception) e));
            e.printStackTrace();
        }

        @Override
        public void onNext(M model) {
            postSuccess(model);
        }
    }
}