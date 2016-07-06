package com.zeyad.cleanarchitecture.presentation.screens;

import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;

/**
 * @author by zeyad on 31/05/16.
 */
public abstract class BasePresenter {
    private final GenericUseCase mGenericUseCase;

    public BasePresenter(GenericUseCase genericUseCase) {
        mGenericUseCase = genericUseCase;
    }

    public abstract void resume();

    public abstract void pause();

    public void destroy() {
        mGenericUseCase.unsubscribe();
    }

    public GenericUseCase getGenericUseCase() {
        return mGenericUseCase;
    }
}