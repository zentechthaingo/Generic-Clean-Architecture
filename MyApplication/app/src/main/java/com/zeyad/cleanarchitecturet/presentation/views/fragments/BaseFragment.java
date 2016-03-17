package com.zeyad.cleanarchitecturet.presentation.views.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import com.zeyad.cleanarchitecturet.presentation.internal.di.HasComponent;

/**
 * Base {@link Fragment} class for every fragment in this application.
 */
public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Shows a {@link Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }
}