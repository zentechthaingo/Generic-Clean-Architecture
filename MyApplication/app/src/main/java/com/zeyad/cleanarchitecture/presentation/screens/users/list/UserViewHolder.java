package com.zeyad.cleanarchitecture.presentation.screens.users.list;

import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.components.AutoLoadImageView;
import com.zeyad.cleanarchitecture.presentation.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
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

    public AutoLoadImageView getAvatar() {
        return mAvatar;
    }

    public RelativeLayout getRl_row_user() {
        return rl_row_user;
    }

    @Override
    public void bindData(Object data, SparseBooleanArray selectedItems, int position) {
        UserViewModel userViewModel = (UserViewModel) data;
        textViewTitle.setText(userViewModel.getFullName());
        rl_row_user.setBackgroundColor(selectedItems.get(position) ? Color.GRAY : Color.WHITE);
    }
}