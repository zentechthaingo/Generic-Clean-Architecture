package com.zeyad.cleanarchitecturet.presentation;

import android.app.Application;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.firebase.client.Firebase;
import com.firebase.client.Logger;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.ApplicationComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.components.DaggerApplicationComponent;
import com.zeyad.cleanarchitecturet.presentation.internal.di.modules.ApplicationModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import okhttp3.OkHttpClient;

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
        initializeStetho();
    }

    private void initializeStetho() {
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
//        Stetho.initialize(Stetho.newInitializerBuilder(this)
//                .enableDumpapp(new DumperPluginsProvider() {
//                    @Override
//                    public Iterable<DumperPlugin> get() {
//                        return new Stetho.DefaultDumperPluginsBuilder(this)
//                                .provide(new MyDumperPlugin())
//                                .finish();
//                    }
//                })
//                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                .build());
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