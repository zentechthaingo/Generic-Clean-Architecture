package com.zeyad.cleanarchitecture.presentation.screens;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.zeyad.cleanarchitecture.domain.exceptions.DefaultErrorBundle;
import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.adapter.rxjava.HttpException;

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
        mGenericPostView.showError(ErrorMessageFactory.create(mGenericPostView.getApplicationContext(),
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
            String message = getErrorMessage(e);
            if (TextUtils.isEmpty(message)) {
                showErrorMessage(new DefaultErrorBundle((Exception) e));
            } else
                mGenericPostView.showError(message);
            e.printStackTrace();
        }

        @Override
        public void onNext(M model) {
            postSuccess(model);
        }
    }

    private String getErrorMessage(Throwable e) {
        String message = "";
        try {
            JSONObject json = new JSONObject(((HttpException) e).response().errorBody().string());
            message = (String) json.getJSONObject("error").get("message");
        } catch (JSONException | IOException e1) {
            e1.printStackTrace();
        }
        return message;
    }
}