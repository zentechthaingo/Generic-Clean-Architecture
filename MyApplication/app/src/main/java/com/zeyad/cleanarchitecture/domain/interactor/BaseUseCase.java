package com.zeyad.cleanarchitecture.domain.interactor;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;

import java.util.Collection;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 * <p>
 * By convention each BaseUseCase implementation will return the result using a {@link rx.Subscriber}
 * that will executeDetail its job in a background thread and will post the result in the UI thread.
 */
public abstract class BaseUseCase {

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;
    private Subscription subscription;

    protected BaseUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        subscription = Subscriptions.empty();
    }

    /**
     * Builds an {@link rx.Observable} which will be used when executing the current {@link BaseUseCase}.
     */
    protected abstract Observable buildUseCaseObservable();

    protected abstract Observable buildUseCaseObservableList(Class presentationClass, Class domainClass,
                                                             Class dataClass);

    protected abstract Observable buildUseCaseObservableDetail(int itemId, Class presentationClass,
                                                               Class domainClass, Class dataClass);

    protected abstract Observable buildUseCaseObservablePut(Object object, Class presentationClass, Class domainClass, Class dataClass);

    protected abstract Observable buildUseCaseObservableDelete(Object object, Class presentationClass,
                                                               Class domainClass, Class dataClass);

    protected abstract Observable buildUseCaseObservableDelete(long itemId, Class presentationClass,
                                                               Class domainClass, Class dataClass);

    protected abstract Observable buildUseCaseObservableDeleteMultiple(Collection collection, Class presentationClass,
                                                                       Class domainClass, Class dataClass);

    protected abstract Observable buildUseCaseObservableQuery(String query, Class presentationClass,
                                                              Class domainClass, Class dataClass);

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void execute(Subscriber UseCaseSubscriber) {
        subscription = buildUseCaseObservable()
//                    .doOnSubscribe(() -> {  /* starting request */
//                        // show Loading Spinner
//                    })
//                    .doOnCompleted(() -> { /* finished request */
//                        // hide Loading Spinner
//                    })
//                    .doOnError(throwable -> {
//                    /* log the error */
//                    })
//                    .onErrorResumeNext(Observable.empty())
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void executeList(Subscriber UseCaseSubscriber, Class presentationClass, Class domainClass, Class dataClass) {
        subscription = buildUseCaseObservableList(presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void executeDetail(Subscriber UseCaseSubscriber, Class presentationClass, Class domainClass, Class dataClass, int id) {
        subscription = buildUseCaseObservableDetail(id, presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void executePut(Subscriber UseCaseSubscriber, Object object, Class presentationClass, Class domainClass, Class dataClass) {
        subscription = buildUseCaseObservablePut(object, presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param charSequence
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public Observable executeSearch(CharSequence charSequence, Subscriber UseCaseSubscriber, Class presentationClass, Class domainClass, Class dataClass) {
        return buildUseCaseObservableQuery(charSequence.toString(), presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle());
//        subscription = buildUseCaseObservableQuery(charSequence.toString(), presentationClass, domainClass, dataClass)
//                .compose(applySchedulers())
//                .compose(getLifecycle())
//                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void executeDelete(Subscriber UseCaseSubscriber, Object object, Class presentationClass, Class domainClass,
                              Class dataClass) {
        subscription = buildUseCaseObservableDelete(object, presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void executeDeleteById(Subscriber UseCaseSubscriber, int id, Class presentationClass, Class domainClass,
                                  Class dataClass) {
        subscription = buildUseCaseObservableDelete(id, presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void executeDeleteCollection(Subscriber UseCaseSubscriber, Collection collection, Class presentationClass, Class domainClass,
                                        Class dataClass) {
        subscription = buildUseCaseObservableDeleteMultiple(collection, presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Unsubscribes from current {@link rx.Subscription}.
     */
    public void unsubscribe() {
        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
    //--------------------------------------------------------------------------------//

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())
                .unsubscribeOn(Schedulers.from(threadExecutor));
    }

    private Observable.Transformer mLifecycle;

    protected void setLifecycle(Observable.Transformer mLifecycle) {
        this.mLifecycle = mLifecycle;
    }

    protected <T> Observable.Transformer<T, T> getLifecycle() {
        return mLifecycle != null ? mLifecycle : o -> o;
    }
}