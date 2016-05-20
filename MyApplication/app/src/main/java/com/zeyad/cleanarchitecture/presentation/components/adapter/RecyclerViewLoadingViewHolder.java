package com.zeyad.cleanarchitecture.presentation.components.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zeyad.cleanarchitecture.R;

import butterknife.ButterKnife;

/**
 * @author by zeyad on 20/05/16.
 */
public class RecyclerViewLoadingViewHolder extends GenericRecyclerViewAdapter.ViewHolder {

    public RecyclerViewLoadingViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.list_loading_layout, parent, false));
        ButterKnife.bind(this, itemView);
    }
}