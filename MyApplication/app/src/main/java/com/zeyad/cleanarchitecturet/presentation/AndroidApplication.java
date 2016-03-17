package com.zeyad.cleanarchitecturet.presentation;

import android.app.Application;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.Logger;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.ApplicationComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.DaggerApplicationComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.modules.ApplicationModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

/**
 * Android Main Application
 */
public class AndroidApplication extends Application {

    private ApplicationComponent applicationComponent;

    private static AndroidApplication androidApplication;

    public static AndroidApplication getInstance() {
        return androidApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        androidApplication = this;
        initializeRealm();
        initializeFirebase();
        initializeInjector();
    }

    private void initializeRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .rxFactory(new RealmObservableFactory())
                .build();
        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);
    }

    private void initializeFirebase() {
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
    }

    private void initializeInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return this.applicationComponent;
    }

    @Override
    public void onTerminate() {
        Toast.makeText(getApplicationContext(), "Good bye!", Toast.LENGTH_SHORT).show();
        super.onTerminate();
    }
}