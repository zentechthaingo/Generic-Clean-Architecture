package com.zeyad.cleanarchitecture.presentation;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.firebase.client.Firebase;
import com.firebase.client.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.ApplicationComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.DaggerApplicationComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.ApplicationModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import okhttp3.OkHttpClient;

/**
 * Android Main Application
 */
public class AndroidApplication extends Application {

    private ApplicationComponent applicationComponent;
    private RefWatcher refWatcher;

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
        initializeLeakCanary();
    }

    private void initializeLeakCanary() {
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((AndroidApplication) context.getApplicationContext()).refWatcher;
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
        return applicationComponent;
    }

    @Override
    public void onTerminate() {
        Toast.makeText(getApplicationContext(), "Good bye!", Toast.LENGTH_SHORT).show();
        super.onTerminate();
    }
}