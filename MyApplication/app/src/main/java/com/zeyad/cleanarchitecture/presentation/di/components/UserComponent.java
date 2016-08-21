package com.zeyad.cleanarchitecture.presentation.di.components;

import com.zeyad.cleanarchitecture.presentation.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.di.modules.ActivityModule;
import com.zeyad.cleanarchitecture.presentation.di.modules.UserModule;
import com.zeyad.cleanarchitecture.presentation.screens.users.details.UserDetailsFragment;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserListActivity;

import dagger.Component;

/**
 * A scope {@link com.zeyad.cleanarchitecture.presentation.di.PerActivity} component.
 * Injects user specific Fragments.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, UserModule.class})
public interface UserComponent extends ActivityComponent {
    void inject(UserListActivity userListActivity);

    void inject(UserDetailsFragment userDetailsFragment);
}