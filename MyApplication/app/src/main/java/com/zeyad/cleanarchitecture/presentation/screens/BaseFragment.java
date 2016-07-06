package com.zeyad.cleanarchitecture.presentation.screens;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.zeyad.cleanarchitecture.domain.eventbus.RxEventBus;
import com.zeyad.cleanarchitecture.presentation.internal.di.HasComponent;
import com.zeyad.cleanarchitecture.utilities.Utils;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Base {@link Fragment} class for every fragment in this application.
 */
public abstract class BaseFragment extends Fragment {

    public CompositeSubscription mCompositeSubscription;
    public RxEventBus rxEventBus;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    public abstract void initialize();

    /**
     * Shows a {@link Toast} message.
     *
     * @param message An string representing a message to be shown.
     */
    protected void showToastMessage(String message) {
        Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets a component for dependency injection by its type.
     */
    @SuppressWarnings("unchecked")
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        Utils.unsubscribeIfNotNull(mCompositeSubscription);
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            rxEventBus = ((BaseActivity) getActivity()).rxEventBus;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage());
        }
    }
}