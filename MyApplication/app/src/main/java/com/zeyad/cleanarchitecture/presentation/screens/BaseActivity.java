package com.zeyad.cleanarchitecture.presentation.screens;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.presentation.AndroidApplication;
import com.zeyad.cleanarchitecture.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.ApplicationComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.DaggerUserComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.UserComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.ActivityModule;
import com.zeyad.cleanarchitecture.presentation.navigation.Navigator;
import com.zeyad.cleanarchitecture.utilities.Constants;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Base {@link Activity} class for every Activity in this application.
 */
public abstract class BaseActivity extends AppCompatActivity implements HasComponent<UserComponent> {
    //    @Inject
    public Navigator navigator;
    @Inject
    public RxEventBus rxEventBus;
    public CompositeSubscription mCompositeSubscription;
    private UserComponent userComponent;
    public FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationComponent().inject(this);
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        initializeInjector();
        initialize();
        setupUI();
        FirebaseCrash.log("Activity created");
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d("TAG", "Key: " + key + " Value: " + value);
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d("TAG", "Subscribed to news topic");
        Log.d("TAG", "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());
    }

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

    /**
     * Setup the UI.
     */
    public abstract void setupUI();

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    protected void addFragment(int containerViewId, Fragment fragment, List<Pair<View, String>> sharedElements) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (sharedElements != null)
            for (Pair<View, String> pair : sharedElements)
                fragmentTransaction.addSharedElement(pair.first, pair.second);
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.add(containerViewId, fragment, fragment.getTag()).commit();
    }

    protected void removeFragment(String tag) {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag(tag))
                .commit();
    }

    /**
     * Get the Main Application component for dependency injection.
     *
     * @return {@link com.zeyad.cleanarchitecture.presentation.internal.di.components.ApplicationComponent}
     */
    protected ApplicationComponent getApplicationComponent() {
        return ((AndroidApplication) getApplicationContext()).getApplicationComponent();
    }

    /**
     * Get an Activity module for dependency injection.
     *
     * @return {@link com.zeyad.cleanarchitecture.presentation.internal.di.modules.ActivityModule}
     */
    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    public void initializeInjector() {
        userComponent = DaggerUserComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();
    }

    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) this).getComponent());
    }

    @Override
    public void onDestroy() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(ComponentCallbacks2.TRIM_MEMORY_COMPLETE);
//        RappiApplication.getRefWatcher(getApplicationContext()).watch(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Observable.defer(() -> Observable.just(saveIsInForeGroundToPrefs(false)))
                .subscribeOn(Schedulers.io())
                .subscribe();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Observable.defer(() -> Observable.just(saveIsInForeGroundToPrefs(true)))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private boolean saveIsInForeGroundToPrefs(boolean isInForeground) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.SETTINGS_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constants.IS_IN_FOREGROUND, isInForeground);
        editor.apply();
        return true;
    }

    /**
     * Shows a {@link Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public UserComponent getComponent() {
        return userComponent;
    }
}