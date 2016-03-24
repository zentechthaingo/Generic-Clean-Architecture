package com.zeyad.cleanarchitecturet.presentation.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zeyad.cleanarchitecturet.R;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecturet.presentation.model.UserModel;
import com.zeyad.cleanarchitecturet.presentation.presenters.GeneralDetailPresenter;
import com.zeyad.cleanarchitecturet.presentation.views.UserDetailsView;
import com.zeyad.cleanarchitecturet.presentation.views.component.AutoLoadImageView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment that shows details of a certain user.
 */
public class UserDetailsFragment extends BaseFragment implements UserDetailsView {

    private static final String ARGUMENT_KEY_USER_ID = "USER_ID";
    private int userId;
    //    @Inject
//    UserDetailsPresenter userDetailsPresenter;
    @Inject
    GeneralDetailPresenter userDetailsPresenter;
    @Bind(R.id.iv_cover)
    AutoLoadImageView iv_cover;
    @Bind(R.id.tv_fullname)
    TextView tv_fullName;
    @Bind(R.id.tv_email)
    TextView tv_email;
    @Bind(R.id.tv_followers)
    TextView tv_followers;
    @Bind(R.id.tv_description)
    TextView tv_description;
    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;
    @Bind(R.id.rl_retry)
    RelativeLayout rl_retry;
    @Bind(R.id.bt_retry)
    Button bt_retry;

    public UserDetailsFragment() {
        super();
    }

    public static UserDetailsFragment newInstance(int userId) {
        UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
        Bundle argumentsBundle = new Bundle();
        argumentsBundle.putInt(ARGUMENT_KEY_USER_ID, userId);
        userDetailsFragment.setArguments(argumentsBundle);
        return userDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_user_details, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        userDetailsPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        userDetailsPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userDetailsPresenter.destroy();
    }

    private void initialize() {
        getComponent(UserComponent.class).inject(this);
        userDetailsPresenter.setView(this);
        userId = getArguments().getInt(ARGUMENT_KEY_USER_ID);
        userDetailsPresenter.initialize(userId);
    }

    @Override
    public void renderUser(UserModel user) {
        if (user != null) {
            iv_cover.setImageUrl(user.getCoverUrl())
                    .setImagePlaceHolder(R.drawable.placer_holder_img)
                    .setImageFallBackResourceId(R.drawable.placer_holder_img)
                    .setImageOnErrorResourceId(R.drawable.placer_holder_img);
            tv_fullName.setText(user.getFullName());
            tv_email.setText(user.getEmail());
            tv_followers.setText(String.valueOf(user.getFollowers()));
            tv_description.setText(user.getDescription());
        }
    }

    @Override
    public void showLoading() {
        rl_progress.setVisibility(View.VISIBLE);
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        rl_progress.setVisibility(View.GONE);
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void showRetry() {
        rl_retry.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetry() {
        rl_retry.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        showToastMessage(message);
    }

    @Override
    public Context getContext() {
        return getActivity().getApplicationContext();
    }

    /**
     * Loads all users.
     */
    private void loadUserDetails() {
        if (userDetailsPresenter != null)
            userDetailsPresenter.initialize(userId);
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        loadUserDetails();
    }
}