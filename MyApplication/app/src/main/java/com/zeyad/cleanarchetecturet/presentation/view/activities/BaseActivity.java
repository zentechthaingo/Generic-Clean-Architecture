package com.zeyad.cleanarchetecturet.presentation.view.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.zeyad.cleanarchetecturet.presentation.AndroidApplication;
import com.zeyad.cleanarchetecturet.presentation.internal.di.components.ApplicationComponent;
import com.zeyad.cleanarchetecturet.presentation.internal.di.modules.ActivityModule;
import com.zeyad.cleanarchetecturet.presentation.navigation.Navigator;

import javax.inject.Inject;

/**
 * Base {@link Activity} class for every Activity in this application.
 */
public abstract class BaseActivity extends Activity {

    @Inject
    Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getApplicationComponent().inject(this);
    }

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link com.zeyad.cleanarchetecturet.presentation.internal.di.components.ApplicationComponent}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplicationContext()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link com.zeyad.cleanarchetecturet.presentation.internal.di.modules.ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }
}