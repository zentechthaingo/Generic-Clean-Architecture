package com.zeyad.cleanarchitecturet.presentation.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zeyad.cleanarchitecturet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ZIaDo on 2/28/16.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.title)
    TextView textViewTitle;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getTextViewTitle() {
        return textViewTitle;
    }

    public void setTextViewTitle(TextView textViewTitle) {
        this.textViewTitle = textViewTitle;
    }
}