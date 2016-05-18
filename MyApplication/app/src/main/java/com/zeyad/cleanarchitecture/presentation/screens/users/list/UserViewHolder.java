package com.zeyad.cleanarchitecture.presentation.screens.users.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.components.AutoLoadImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.title)
    TextView textViewTitle;
    @Bind(R.id.avatar)
    AutoLoadImageView mAvatar;
    @Bind(R.id.rl_row_user)
    RelativeLayout rl_row_user;

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

    public AutoLoadImageView getmAvatar() {
        return mAvatar;
    }

    public RelativeLayout getRl_row_user() {
        return rl_row_user;
    }
}