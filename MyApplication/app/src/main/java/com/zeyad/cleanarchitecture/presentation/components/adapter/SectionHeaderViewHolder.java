package com.zeyad.cleanarchitecture.presentation.components.adapter;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeyad.cleanarchitecture.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author by zeyad on 13/06/16.
 */
public class SectionHeaderViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @Bind(R.id.tvSectionHeader)
    TextView tvSectionHeader;

    public SectionHeaderViewHolder(LayoutInflater layoutInflater, ViewGroup parent) {
        super(layoutInflater.inflate(R.layout.list_section_header_layout, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, SparseBooleanArray selectedItems, int position) {
        if (data instanceof String)
            tvSectionHeader.setText((String) data);
    }
}