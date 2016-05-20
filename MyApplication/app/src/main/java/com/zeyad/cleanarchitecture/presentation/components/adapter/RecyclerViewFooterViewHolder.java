package com.zeyad.cleanarchitecture.presentation.components.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeyad.cleanarchitecture.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author by zeyad on 19/05/16.
 */
public class RecyclerViewFooterViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @Bind(R.id.tvFooter)
    TextView tvFooter;

    public RecyclerViewFooterViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.list_footer_layout, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, SparseBooleanArray selectedItems, int position) {
        tvFooter.setText((String) data);
    }
}