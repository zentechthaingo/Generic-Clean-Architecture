package com.zeyad.cleanarchitecture.presentation.views.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.utilities.Constants;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Main application screen. This is the app entry point.
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.btn_LoadData)
    Button btn_LoadData;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());
        // TODO: 1/29/16 Revisit!
        Constants.CACHE_DIR = new File(String.valueOf(getCacheDir())).getAbsolutePath();
    }

    /**
     * Goes to the user list screen.
     */
    @OnClick(R.id.btn_LoadData)
    void navigateToUserList() {
        this.navigator.navigateToUserList(this);
    }
}