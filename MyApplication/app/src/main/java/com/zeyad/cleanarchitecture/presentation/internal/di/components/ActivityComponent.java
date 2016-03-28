package com.zeyad.cleanarchitecture.presentation.internal.di.components;

import android.support.v7.app.AppCompatActivity;

import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.internal.di.modules.ActivityModule;
import com.zeyad.cleanarchitecture.presentation.presenters.GeneralListPresenter;

import dagger.Component;

/**
 * A base component upon which fragment's components may depend.
 * Activity-level components should extend this component.
 * <p>
 * Subtypes of ActivityComponent should be decorated with annotation:
 * {@link com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity}
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    //Exposed to sub-graphs.
    AppCompatActivity activity();
}