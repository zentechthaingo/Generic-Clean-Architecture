package com.zeyad.cleanarchitecturet.presentation.internal.di.components;

import com.zeyad.cleanarchitecturet.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecturet.presentation.internal.di.modules.ActivityModule;
import com.zeyad.cleanarchitecturet.presentation.internal.di.modules.UserModule;
import com.zeyad.cleanarchitecturet.presentation.view.fragments.UserDetailsFragment;
import com.zeyad.cleanarchitecturet.presentation.view.fragments.UserListFirebaseFragment;
import com.zeyad.cleanarchitecturet.presentation.view.fragments.UserListFragment;

import dagger.Component;

/**
 * A scope {@link com.zeyad.cleanarchitecturet.presentation.internal.di.PerActivity} component.
 * Injects user specific Fragments.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, UserModule.class})
public interface UserComponent extends ActivityComponent {
    void inject(UserListFragment userListFragment);

    void inject(UserListFirebaseFragment userListFirebaseFragment);

    void inject(UserDetailsFragment userDetailsFragment);
}