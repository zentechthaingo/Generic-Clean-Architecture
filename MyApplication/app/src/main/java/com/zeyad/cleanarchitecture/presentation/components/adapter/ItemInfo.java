package com.zeyad.cleanarchitecture.presentation.components.adapter;

/**
 * @author by zeyad on 20/05/16.
 */
public abstract class ItemInfo<M> {
    public static final int HEADER = 1, FOOTER = 2, LOADING = 3;
    private M data;
    private int layoutId;

    public ItemInfo(M data, int layoutId) {
        this.data = data;
        this.layoutId = layoutId;
    }

    public abstract long getId();

    public M getData() {
        return data;
    }

    public int getLayoutId() {
        return layoutId;
    }
}