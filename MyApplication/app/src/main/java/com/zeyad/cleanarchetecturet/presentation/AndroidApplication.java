package com.zeyad.cleanarchetecturet.presentation;

import android.app.Application;
import android.widget.Toast;

import com.zeyad.cleanarchetecturet.presentation.internal.di.components.ApplicationComponent;
import com.zeyad.cleanarchetecturet.presentation.internal.di.components.DaggerApplicationComponent;
import com.zeyad.cleanarchetecturet.presentation.internal.di.modules.ApplicationModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Android Main Application
 */
public class AndroidApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(config);
        initializeInjector();
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
//        Realm realm = Realm.getDefaultInstance();
//        realm.close();
        Toast.makeText(getApplicationContext(), "Good bye!", Toast.LENGTH_SHORT).show();
        super.onTerminate();
    }
}