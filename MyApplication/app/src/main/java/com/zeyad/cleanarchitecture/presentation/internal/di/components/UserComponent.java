package com.zeyad.cleanarchitecture.presentation.internal.di.components;

import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.ActivityModule;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.UserModule;
import com.zeyad.cleanarchitecture.presentation.views.activities.UserListActivity;
import com.zeyad.cleanarchitecture.presentation.views.fragments.UserDetailsFragment;

import dagger.Component;

/**
 * A scope {@link com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity} component.
 * Injects user specific Fragments.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, UserModule.class})
public interface UserComponent extends ActivityComponent {
    void inject(UserListActivity userListActivity);

    void inject(UserDetailsFragment userDetailsFragment);
}