package com.zeyad.cleanarchitecture.presentation.screens;

/**
 * @author by zeyad on 23/05/16.
 */
public interface GenericPostView<M> extends LoadDataView {

    void postSuccessful(M model);
}