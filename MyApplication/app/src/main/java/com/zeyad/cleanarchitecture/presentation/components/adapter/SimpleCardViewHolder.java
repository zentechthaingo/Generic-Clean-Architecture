package com.zeyad.cleanarchitecture.presentation.components.adapter;

import android.support.v4.util.Pair;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;

import com.zeyad.cleanarchitecture.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author zeyad on 8/3/16.
 */
public class SimpleCardViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @Bind(R.id.tvSimpleText)
    TextView tvSimpleText;
    @Bind(R.id.tvSimpleTextValue)
    TextView tvSimpleTextValue;

    public SimpleCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, SparseBooleanArray selectedItems, int position, boolean isEnabled) {
        itemView.setEnabled(isEnabled);
        if (data instanceof String) {
            tvSimpleText.setText((String) data);
            tvSimpleTextValue.setText("");
        } else if (data instanceof Pair) {
            Pair pair = (Pair) data;
            tvSimpleText.setText((String) pair.first);
            if (pair.second instanceof String) {
                tvSimpleTextValue.setText((String) pair.second);
            } else if (pair.second != null) {
                tvSimpleTextValue.setText(String.valueOf(pair.second));
            } else {
                tvSimpleTextValue.setText("");
            }
        }
    }
}