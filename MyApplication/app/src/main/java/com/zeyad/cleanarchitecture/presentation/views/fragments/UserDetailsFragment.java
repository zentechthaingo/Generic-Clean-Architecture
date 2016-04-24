package com.zeyad.cleanarchitecture.presentation.views.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.presenters.GenericDetailPresenter;
import com.zeyad.cleanarchitecture.presentation.views.UserDetailsView;
import com.zeyad.cleanarchitecture.presentation.views.activities.UserDetailsActivity;
import com.zeyad.cleanarchitecture.utilities.Utils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;
// TODO: 4/15/16 Add image upload!

/**
 * Fragment that shows details of a certain user.
 */
public class UserDetailsFragment extends BaseFragment implements UserDetailsView {

    public static final String ARGUMENT_KEY_USER_ID = "USER_ID", ARG_ITEM_ID = "item_id",
            ARG_ITEM_IMAGE = "item_image", ARG_ITEM_NAME = "item_name", ADD_NEW_ITEM = "add_new_item";
    private int userId;
    @Inject
    GenericDetailPresenter userDetailsPresenter;
    @Bind(R.id.tv_fullname)
    TextView tv_fullName;
    @Bind(R.id.et_full_name)
    EditText et_fullName;
    @Bind(R.id.tv_email)
    TextView tv_email;
    @Bind(R.id.et_email)
    EditText et_email;
    @Bind(R.id.tv_followers)
    TextView tv_followers;
    @Bind(R.id.et_followers)
    EditText et_followers;
    @Bind(R.id.tv_description)
    TextView tv_description;
    @Bind(R.id.et_description)
    EditText et_description;
    @Bind(R.id.rl_progress)
    RelativeLayout rl_progress;
    @Bind(R.id.rl_retry)
    RelativeLayout rl_retry;
    @Bind(R.id.ll_edit)
    LinearLayout ll_edit;
    @Bind(R.id.ll_item_details)
    LinearLayout ll_item_details;
    @Bind(R.id.bt_retry)
    Button bt_retry;
    private UserModel userModel;
    private Observable<CharSequence> fullNameChangeObservable;
    private Observable<CharSequence> emailChangeObservable;
    private Observable<CharSequence> followersChangeObservable;
    private Subscription formSubscription;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        Utils.unsubscribeIfNotNull(formSubscription);
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
        if (userId == -1) {
            ((UserDetailsActivity) getActivity()).getEditDetailsFab().setImageResource(R.drawable.ic_done);
            userDetailsPresenter.setupEdit();
        } else
            userDetailsPresenter.initialize(userId);
    }

    @Override
    public void renderUser(UserModel user) {
        if (user != null) {
            if (user.getCoverUrl() != null)
                ((UserDetailsActivity) getActivity()).mDetailImage
                        .setImageUrl(user.getCoverUrl())
                        .setImagePlaceHolder(R.drawable.placer_holder_img)
                        .setImageFallBackResourceId(R.drawable.placer_holder_img)
                        .setImageOnErrorResourceId(R.drawable.placer_holder_img);
            tv_fullName.setText(user.getFullName());
            tv_email.setText(user.getEmail());
            tv_followers.setText(String.valueOf(user.getFollowers()));
            tv_description.setText(user.getDescription());
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().
                    findViewById(R.id.toolbar_layout);
            if (appBarLayout != null)
                appBarLayout.setTitle(user.getFullName());
//            applyPalette();
        }
    }

    @Override
    public void editUser(UserModel userModel) {
        ll_item_details.setVisibility(View.GONE);
        ll_edit.setVisibility(View.VISIBLE);
        fullNameChangeObservable = RxTextView.textChanges(et_fullName).skip(1);
        emailChangeObservable = RxTextView.textChanges(et_email).skip(1);
        followersChangeObservable = RxTextView.textChanges(et_followers).skip(1);
        validateForm();
        if (userModel != null) {
            this.userModel = userModel;
            et_fullName.setText(userModel.getFullName());
            et_email.setText(userModel.getEmail());
            et_followers.setText(String.valueOf(userModel.getFollowers()));
            et_description.setText(userModel.getDescription());
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().
                    findViewById(R.id.toolbar_layout);
            if (appBarLayout != null)
                appBarLayout.setTitle(userModel.getFullName());
//            applyPalette();
        }
    }

    @Override
    public void editUserSubmit() {
        ll_edit.setVisibility(View.GONE);
        renderUser(userModel);
    }

    @Override
    public UserModel getValidatedUser() {
        if (userModel != null)
                userModel.setUserId(userModel.getUserId());
        if (userModel == null)
            userModel = new UserModel();
        userModel.setFullName(et_fullName.getText().toString());
        userModel.setEmail(et_email.getText().toString());
        userModel.setFollowers(Integer.parseInt(et_followers.getText().toString()));
        userModel.setDescription(et_description.getText().toString());
        return userModel;
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

    public GenericDetailPresenter getUserDetailsPresenter() {
        return userDetailsPresenter;
    }

    private void validateForm() {
        formSubscription = Observable.combineLatest(emailChangeObservable, followersChangeObservable,
                fullNameChangeObservable, (newEmail, newFollowers, newFullName) -> {
                    boolean emailValid = !isEmpty(newEmail) &&
                            EMAIL_ADDRESS.matcher(newEmail).matches();
                    if (!emailValid)
                        tv_email.setError("Invalid Email or empty!");
                    boolean passValid = !isEmpty(newFullName);
                    if (!passValid)
                        et_fullName.setError("Name shouldn't be empty!");
                    boolean numValid = !isEmpty(newFollowers);
                    if (numValid)
                        numValid = Integer.parseInt(newFollowers.toString()) > 0;
                    if (!numValid)
                        et_followers.setError("Invalid Number!");
                    return emailValid && passValid && numValid;
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
//                        Timber.d("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Timber.e(e, "there was an error");
                    }

                    @Override
                    public void onNext(Boolean formValid) {
                        if (formValid)
                            ((UserDetailsActivity) getActivity()).getEditDetailsFab().setEnabled(true);
                        else
                            ((UserDetailsActivity) getActivity()).getEditDetailsFab().setEnabled(false);
                    }
                });
    }

    private void applyPalette() {
        if (Utils.hasM())
            Palette.from(((UserDetailsActivity) getActivity()).mDetailImage.getBitmap()).
                    generate(palette -> ((UserDetailsActivity) getActivity()).mCoordinatorLayout
                            .setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                                if (v.getHeight() == scrollX) {
                                    ((UserDetailsActivity) getActivity()).mToolbar
                                            .setTitleTextColor(palette.getLightVibrantColor(Color.TRANSPARENT));
                                    ((UserDetailsActivity) getActivity()).mToolbar.
                                            setBackground(new ColorDrawable(palette
                                                    .getLightVibrantColor(Color.TRANSPARENT)));
                                } else if (scrollY == 0) {
                                    ((UserDetailsActivity) getActivity()).mToolbar.setTitleTextColor(0);
                                    ((UserDetailsActivity) getActivity()).mToolbar.setBackground(null);
                                }
                            }));
    }
}