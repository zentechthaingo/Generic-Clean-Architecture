package com.zeyad.cleanarchitecturet.presentation.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.zeyad.cleanarchitecturet.R;
import com.zeyad.cleanarchitecturet.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.DaggerUserComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecturet.presentation.model.UserModel;
import com.zeyad.cleanarchitecturet.presentation.views.fragments.UserListFragment;

/**
 * Activity that shows a list of Users.
 */
public class UserListActivity extends BaseActivity implements HasComponent<UserComponent>,
        UserListFragment.UserListListener {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    private UserComponent userComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initializeInjector();
    }

    private void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }


    @Override
    public UserComponent getComponent() {
        return userComponent;
    }

    @Override
    public void onUserClicked(UserModel userModel) {
        navigator.navigateToUserDetails(this, userModel.getUserId());
//        if (mTwoPane) {
//            Bundle arguments = new Bundle();
//            arguments.putString(ProductDetailFragment.ARG_ITEM_ID, holder.mProduct.getProduct_id());
//            arguments.putString(ProductDetailFragment.ARG_ITEM_IMAGE, holder.mProduct.getImage());
//            arguments.putString(ProductDetailFragment.ARG_ITEM_NAME, holder.mProduct.getName()
//                    .toLowerCase());
//            ProductDetailFragment fragment = new ProductDetailFragment();
//            fragment.setArguments(arguments);
//            mContext.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.product_detail_container, fragment).commit();
//        } else {
//            Intent intent = new Intent(mContext, ProductDetailActivity.class);
//            intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, holder.mProduct.getProduct_id());
//            intent.putExtra(ProductDetailFragment.ARG_ITEM_IMAGE, holder.mProduct.getImage());
//            intent.putExtra(ProductDetailFragment.ARG_ITEM_NAME, holder.mProduct.getName()
//                    .toLowerCase());
//            if (Utils.hasLollipop()) {
//                Pair<View, String> pair = new Pair<View, String>(holder.mImageView, holder.mImageView.getTransitionName());
//                Pair<View, String> secondPair = new Pair<View, String>(holder.mPriceView, holder.mPriceView.getTransitionName());
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(mContext, pair, secondPair);
//                mContext.startActivity(intent, options.toBundle());
//            } else mContext.startActivity(intent);
//        }
    }
}