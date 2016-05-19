package com.zeyad.cleanarchitecture.presentation;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.scand.realmbrowser.RealmBrowser;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zeyad.cleanarchitecture.BuildConfig;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.ApplicationComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.components.DaggerApplicationComponent;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.ApplicationModule;
import com.zeyad.cleanarchitecture.utilities.Constants;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

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
//        initializeFirebase();
        initializeInjector();
        initializeLeakCanary();
        if (BuildConfig.DEBUG)
            initializeStetho();
        Constants.CACHE_DIR = getCacheDir().getAbsolutePath();
    }

    private void initializeLeakCanary() {
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        return ((AndroidApplication) context.getApplicationContext()).refWatcher;
    }

    private void initializeStetho() {
//        Stetho.initializeWithDefaults(this);
//        new OkHttpClient.Builder()
//                .addNetworkInterceptor(new StethoInterceptor())
//                .build();
//        Stetho.initialize(Stetho.newInitializerBuilder(this)
//                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                .build());
    }

    private void initializeRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        if (BuildConfig.DEBUG)
            new RealmBrowser.Builder(this)
                    .add(Realm.getDefaultInstance(), UserRealmModel.class) // add class, you want to view
                    .showNotification(); // call method showNotification()
    }

//    private void initializeFirebase() {
//        Firebase.setAndroidContext(this);
//        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
//    }

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