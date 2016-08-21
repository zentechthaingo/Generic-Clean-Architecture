package com.zeyad.cleanarchitecture.presentation.screens;

import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.utilities.Utils;

import rx.subscriptions.CompositeSubscription;

/**
 * @author by zeyad on 31/05/16.
 */
public abstract class BasePresenter {
    private final GenericUseCase mGenericUseCase;
    private CompositeSubscription mCompositeSubscription;

    public BasePresenter(GenericUseCase genericUseCase) {
        mGenericUseCase = genericUseCase;
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    public abstract void resume();

    public abstract void pause();

    public void destroy() {
        mGenericUseCase.unsubscribe();
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    public GenericUseCase getGenericUseCase() {
        return mGenericUseCase;
    }
}