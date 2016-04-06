package com.zeyad.cleanarchitecture.presentation.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.views.component.AutoLoadImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.title)
    TextView textViewTitle;
    @Bind(R.id.selected_overlay)
    View mSelectedOverlay;
    @Bind(R.id.avatar)
    AutoLoadImageView mAvatar;


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

    public View getmSelectedOverlay() {
        return mSelectedOverlay;
    }

    public void setmSelectedOverlay(View mSelectedOverlay) {
        this.mSelectedOverlay = mSelectedOverlay;
    }

    public AutoLoadImageView getmAvatar() {
        return mAvatar;
    }
}