package com.zeyad.cleanarchitecture.domain.interactors;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.mapper.ModelDataMapper;
import com.zeyad.cleanarchitecture.domain.repository.Repository;

import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

import io.realm.RealmQuery;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * This class is a general implementation that represents a use case for retrieving data.
 */
public class GenericUseCase {

    private final Repository mRepository;
    private final ModelDataMapper mModelDataMapper;
    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;
    private Subscription subscription;

    @Inject
    public GenericUseCase(Repository repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        subscription = Subscriptions.empty();
        mRepository = repository;
        mModelDataMapper = new ModelDataMapper(new Gson());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicGetList(Subscriber UseCaseSubscriber, String url, Class presentationClass,
                                      Class domainClass, Class dataClass, boolean persist) {
        subscription = mRepository.getListDynamically(url, domainClass, dataClass, persist)
                .map(collection -> mModelDataMapper.transformAllToPresentation(collection, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicGetList(Subscriber UseCaseSubscriber, String url, Class presentationClass,
                                      Class domainClass, Class dataClass, boolean persist, boolean shouldCache) {
        subscription = mRepository.getListDynamically(url, domainClass, dataClass, persist, shouldCache)
                .map(collection -> mModelDataMapper.transformAllToPresentation(collection, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeGetObject(Subscriber UseCaseSubscriber, String url, String idColumnName, int itemId,
                                 Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
        subscription = mRepository.getObjectDynamicallyById(url, idColumnName, itemId, domainClass, dataClass, persist)
                .map(item -> mModelDataMapper.transformToPresentation(item, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeGetObject(Subscriber UseCaseSubscriber, String url, String idColumnName, int itemId, Class presentationClass,
                                 Class domainClass, Class dataClass, boolean persist, boolean shouldCache) {
        subscription = mRepository.getObjectDynamicallyById(url, idColumnName, itemId, domainClass, dataClass,
                persist, shouldCache)
                .map(item -> mModelDataMapper.transformToPresentation(item, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPostObject(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                         Class presentationClass, Class domainClass, Class dataClass,
                                         boolean persist) {
        subscription = mRepository.postObjectDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPostObject(Subscriber UseCaseSubscriber, String url, JSONObject keyValuePairs,
                                         Class presentationClass, Class domainClass, Class dataClass,
                                         boolean persist) {
        subscription = mRepository.postObjectDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPostList(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                       Class presentationClass, Class domainClass, Class dataClass,
                                       boolean persist) {
        subscription = mRepository.postListDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass))
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
    public void executeSearch(Subscriber UseCaseSubscriber, String query, String column,
                              Class presentationClass, Class domainClass, Class dataClass) {
        subscription = mRepository.searchDisk(query, column, domainClass, dataClass)
                .map(list -> mModelDataMapper.transformAllToPresentation(list, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param realmQuery
     */
    @SuppressWarnings("unchecked")
    public void executeSearch(Subscriber UseCaseSubscriber, RealmQuery realmQuery, Class presentationClass,
                              Class domainClass) {
        subscription = mRepository.searchDisk(realmQuery, domainClass)
                .map(list -> mModelDataMapper.transformAllToPresentation(list, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDeleteCollection(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                        Class domainClass, Class dataClass, boolean persist) {
        subscription = mRepository.deleteListDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPutObject(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                        Class presentationClass, Class domainClass, Class dataClass,
                                        boolean persist) {
        subscription = mRepository.putObjectDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPutList(Subscriber UseCaseSubscriber, String url, HashMap<String, Object> keyValuePairs,
                                      Class presentationClass, Class domainClass, Class dataClass,
                                      boolean persist) {
        subscription = mRepository.putListDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicDeleteAll(Subscriber UseCaseSubscriber, String url, Class dataClass,
                                        boolean persist) {
        subscription = mRepository.deleteAllDynamically(url, dataClass, persist)
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