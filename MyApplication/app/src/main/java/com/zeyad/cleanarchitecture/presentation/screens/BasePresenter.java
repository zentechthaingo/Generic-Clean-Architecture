package com.zeyad.cleanarchitecture.presentation.screens;

import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;

/**
 * Abstract class representing a BasePresenter in a model view presenter (MVP) pattern.
 */
public abstract class BasePresenter {
    public final GenericUseCase mGenericUseCase;

    public BasePresenter(GenericUseCase genericUseCase) {
        mGenericUseCase = genericUseCase;
    }
    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onResume() method.
     */
    public abstract void resume();
    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onPause() method.
     */
    public abstract void pause();
    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onDestroy() method.
     */
    public void destroy() {
        mGenericUseCase.unsubscribe();
    }
}