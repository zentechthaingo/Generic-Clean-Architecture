package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 * <p/>
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
     * Builds an {@link Observable} which will be used when executing the current {@link BaseUseCase}.
     */
    protected abstract Observable buildUseCaseObservableDynamicList(String url, Class presentationClass,
                                                                    Class domainClass, Class dataClass,
                                                                    boolean persist);

    protected abstract Observable buildUseCaseObservableDynamicObjectById(String url, int itemId,
                                                                          Class presentationClass,
                                                                          Class domainClass,
                                                                          Class dataClass, boolean persist);

    protected abstract Observable buildUseCaseObservablePut(String url, HashMap<String, Object> keyValuePairs,
                                                            Class presentationClass, Class domainClass,
                                                            Class dataClass, boolean persist);

    protected abstract Observable buildUseCaseObservableDynamicPostList(String url, HashMap<String, Object> keyValuePairs,
                                                                        Class presentationClass, Class domainClass,
                                                                        Class dataClass, boolean persist);

    protected abstract Observable buildUseCaseObservableDeleteMultiple(String url, HashMap<String, Object> keyValuePairs,
                                                                       Class domainClass, Class dataClass,
                                                                       boolean persist);

    protected abstract Observable buildUseCaseObservableQuery(String query, String column, Class presentationClass,
                                                              Class domainClass, Class dataClass);

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservableDynamicList}.
     */
    @SuppressWarnings("unchecked")
    public void executeList(Subscriber UseCaseSubscriber, String url, Class presentationClass,
                            Class domainClass, Class dataClass, boolean persist) {
        subscription = buildUseCaseObservableDynamicList(url, presentationClass, domainClass, dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservableDynamicObjectById}.
     */
    @SuppressWarnings("unchecked")
    public void executeGetObject(Subscriber UseCaseSubscriber, String url, int id, Class presentationClass,
                                 Class domainClass, Class dataClass, boolean persist) {
        subscription = buildUseCaseObservableDynamicObjectById(url, id, presentationClass, domainClass,
                dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservablePut}.
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPutObject(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                        Class presentationClass, Class domainClass, Class dataClass,
                                        boolean persist) {
        subscription = buildUseCaseObservablePut(url, keyValuePairs, presentationClass, domainClass,
                dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservableDynamicPostList}.
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPostList(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                       Class presentationClass, Class domainClass, Class dataClass,
                                       boolean persist) {
        subscription = buildUseCaseObservableDynamicPostList(url, keyValuePairs, presentationClass,
                domainClass, dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param query
     */
    @SuppressWarnings("unchecked")
    public void executeSearch(String query, String column, Subscriber UseCaseSubscriber,
                              Class presentationClass, Class domainClass, Class dataClass) {
        subscription = buildUseCaseObservableQuery(query, column, presentationClass, domainClass, dataClass)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservableDeleteMultiple}.
     */
    @SuppressWarnings("unchecked")
    public void executeDeleteCollection(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                        Class domainClass, Class dataClass, boolean persist) {
        subscription = buildUseCaseObservableDeleteMultiple(url, keyValuePairs, domainClass, dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Unsubscribes from current {@link Subscription}.
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