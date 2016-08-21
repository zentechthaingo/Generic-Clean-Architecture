package com.zeyad.cleanarchitecture.presentation;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import com.zeyad.cleanarchitecture.BuildConfig;
import com.zeyad.cleanarchitecture.presentation.di.components.ApplicationComponent;
import com.zeyad.cleanarchitecture.presentation.di.components.DaggerApplicationComponent;
import com.zeyad.cleanarchitecture.presentation.di.modules.ApplicationModule;
import com.zeyad.cleanarchitecture.utilities.Constants;

import java.util.regex.Pattern;

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
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                .build());
        RealmInspectorModulesProvider.builder(this)
                .withFolder(getCacheDir())
//                .withEncryptionKey("encrypted.realm", key)
                .withMetaTables()
                .withDescendingOrder()
                .withLimit(1000)
                .databaseNamePattern(Pattern.compile(".+\\.realm"))
                .build();
    }

    private void initializeRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

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
}