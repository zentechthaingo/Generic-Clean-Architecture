package com.zeyad.cleanarchetecturet.presentation.view.activity;

import android.os.Bundle;
import android.widget.Button;

import com.zeyad.cleanarchetecturet.R;
import com.zeyad.cleanarchetecturet.utilities.Constants;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
