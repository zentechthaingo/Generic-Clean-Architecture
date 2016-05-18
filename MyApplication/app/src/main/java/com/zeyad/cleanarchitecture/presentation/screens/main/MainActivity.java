package com.zeyad.cleanarchitecture.presentation.screens.main;

import android.os.Bundle;
import android.widget.Button;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.screens.BaseActivity;
import com.zeyad.cleanarchitecture.utilities.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Main application screen. This is the app entry point.
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.btn_LoadData)
    Button btn_LoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Constants.CACHE_DIR = getCacheDir().getAbsolutePath();
    }

    /**
     * Goes to the user list screen.
     */
    @OnClick(R.id.btn_LoadData)
    void navigateToUserList() {
        navigator.navigateToUserList(this);
    }
}