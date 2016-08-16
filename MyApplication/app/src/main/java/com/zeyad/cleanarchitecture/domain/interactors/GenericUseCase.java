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
public class GenericUseCase  {

    private final Repository mRepository;
    @NonNull
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
    @Deprecated
    public void executeDynamicGetList(@NonNull Subscriber UseCaseSubscriber, String url, @NonNull Class presentationClass,
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
     * @param genericUseCaseRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicGetList(@NonNull GetListRequest genericUseCaseRequest) throws Exception {
        subscription = mRepository.getListDynamically(genericUseCaseRequest.getUrl(), genericUseCaseRequest.getDomainClass(),
                genericUseCaseRequest.getDataClass(), genericUseCaseRequest.isPersist(), genericUseCaseRequest.isShouldCache())
                .map(collection -> mModelDataMapper.transformAllToPresentation(collection, genericUseCaseRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(genericUseCaseRequest.getSubscriber());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeDynamicGetList(@NonNull Subscriber UseCaseSubscriber, String url, @NonNull Class presentationClass,
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
     * @param genericUseCaseRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public Observable getList(@NonNull GetListRequest genericUseCaseRequest) {
        return mRepository.getListDynamically(genericUseCaseRequest.getUrl(), genericUseCaseRequest.getDomainClass(),
                genericUseCaseRequest.getDataClass(), genericUseCaseRequest.isPersist(), genericUseCaseRequest.isShouldCache())
                .map(collection -> mModelDataMapper.transformAllToPresentation(collection, genericUseCaseRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeGetObject(@NonNull Subscriber UseCaseSubscriber, String url, String idColumnName, int itemId,
                                 @NonNull Class presentationClass, Class domainClass, Class dataClass, boolean persist) {
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
    @Deprecated
    public void executeGetObject(@NonNull Subscriber UseCaseSubscriber, String url, String idColumnName, int itemId,
                                 @NonNull Class presentationClass, Class domainClass, Class dataClass, boolean persist,
                                 boolean shouldCache) {
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
     * @param getObjectRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeGetObject(@NonNull GetObjectRequest getObjectRequest) {
        subscription = mRepository.getObjectDynamicallyById(getObjectRequest.getUrl(), getObjectRequest.getIdColumnName(),
                getObjectRequest.getItemId(), getObjectRequest.getDomainClass(), getObjectRequest.getDataClass(),
                getObjectRequest.isPersist(), getObjectRequest.isShouldCache())
                .map(item -> mModelDataMapper.transformToPresentation(item, getObjectRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(getObjectRequest.getSubscriber());
    }

    /**
     * Executes the current use case.
     *
     * @param getObjectRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public Observable getObject(@NonNull GetObjectRequest getObjectRequest) {
        return mRepository.getObjectDynamicallyById(getObjectRequest.getUrl(), getObjectRequest.getIdColumnName(),
                getObjectRequest.getItemId(), getObjectRequest.getDomainClass(), getObjectRequest.getDataClass(),
                getObjectRequest.isPersist(), getObjectRequest.isShouldCache())
                .map(item -> mModelDataMapper.transformToPresentation(item, getObjectRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeDynamicPostObject(@NonNull Subscriber UseCaseSubscriber, String url, HashMap<String,
            Object> keyValuePairs, @NonNull Class presentationClass, Class domainClass, Class dataClass,
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
    @Deprecated
    public void executeDynamicPostObject(@NonNull Subscriber UseCaseSubscriber, String url, JSONObject keyValuePairs,
                                         @NonNull Class presentationClass, Class domainClass, Class dataClass,
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
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPostObject(@NonNull PostRequest postRequest) {
        subscription = mRepository.postObjectDynamically(postRequest.getUrl(), postRequest.getKeyValuePairs(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, postRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(postRequest.getSubscriber());
    }

    public Observable postObject(@NonNull PostRequest postRequest) {
        return mRepository.postObjectDynamically(postRequest.getUrl(), postRequest.getKeyValuePairs(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, postRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeDynamicPostList(@NonNull Subscriber UseCaseSubscriber, String url, JSONArray jsonArray,
                                       Class domainClass, Class dataClass, boolean persist) {
        subscription = mRepository.postListDynamically(url, jsonArray, domainClass, dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPostList(@NonNull PostRequest postRequest) {
        subscription = mRepository.postListDynamically(postRequest.getUrl(), postRequest.getJsonArray(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(postRequest.getSubscriber());
    }

    public Observable postList(@NonNull PostRequest postRequest) {
        return mRepository.postListDynamically(postRequest.getUrl(), postRequest.getJsonArray(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param query
     */
    @SuppressWarnings("unchecked")
    public void executeSearch(@NonNull Subscriber UseCaseSubscriber, String query, String column,
                              @NonNull Class presentationClass, Class domainClass, Class dataClass) {
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
    public void executeSearch(@NonNull Subscriber UseCaseSubscriber, RealmQuery realmQuery, @NonNull Class presentationClass,
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
    @Deprecated
    public void executeDeleteCollection(@NonNull Subscriber UseCaseSubscriber, String url, HashMap<String,
            Object> keyValuePairs, Class domainClass, Class dataClass, boolean persist) {
        subscription = mRepository.deleteListDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param deleteRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDeleteCollection(@NonNull PostRequest deleteRequest) {
        subscription = mRepository.deleteListDynamically(deleteRequest.getUrl(), deleteRequest.getKeyValuePairs(),
                deleteRequest.getDomainClass(), deleteRequest.getDataClass(), deleteRequest.isPersist())
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(deleteRequest.getSubscriber());
    }

    public Observable deleteCollection(@NonNull PostRequest deleteRequest) {
        return mRepository.deleteListDynamically(deleteRequest.getUrl(), deleteRequest.getKeyValuePairs(),
                deleteRequest.getDomainClass(), deleteRequest.getDataClass(), deleteRequest.isPersist())
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPutObject(@NonNull PostRequest postRequest) {
        subscription = mRepository.putObjectDynamically(postRequest.getUrl(), postRequest.getKeyValuePairs(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, postRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(postRequest.getSubscriber());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeDynamicPutObject(@NonNull Subscriber UseCaseSubscriber, String url, HashMap<String,
            Object> keyValuePairs, @NonNull Class presentationClass, Class domainClass, Class dataClass,
                                        boolean persist) {
        subscription = mRepository.putObjectDynamically(url, keyValuePairs, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    public Observable putObject(@NonNull PostRequest postRequest) {
        return mRepository.putObjectDynamically(postRequest.getUrl(), postRequest.getKeyValuePairs(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, postRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeUploadFile(@NonNull Subscriber UseCaseSubscriber, String url, File file, @NonNull Class presentationClass,
                                  Class domainClass, Class dataClass, boolean persist) {
        subscription = mRepository.uploadFileDynamically(url, file, domainClass, dataClass, persist)
                .map(object -> mModelDataMapper.transformToPresentation(object, presentationClass))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param uploadRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeUploadFile(@NonNull UploadRequest uploadRequest) {
        subscription = mRepository.uploadFileDynamically(uploadRequest.getUrl(), uploadRequest.getFile(),
                uploadRequest.getDomainClass(), uploadRequest.getDataClass(), uploadRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, uploadRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(uploadRequest.getSubscriber());
    }

    public Observable uploadFile(@NonNull UploadRequest uploadRequest) {
        return mRepository.uploadFileDynamically(uploadRequest.getUrl(), uploadRequest.getFile(),
                uploadRequest.getDomainClass(), uploadRequest.getDataClass(), uploadRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, uploadRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeDynamicPutList(@NonNull Subscriber UseCaseSubscriber, String url, HashMap<String,
            Object> keyValuePairs, @NonNull Class presentationClass, Class domainClass, Class dataClass,
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
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicPutList(@NonNull PostRequest postRequest) {
        subscription = mRepository.putListDynamically(postRequest.getUrl(), postRequest.getKeyValuePairs(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, postRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(postRequest.getSubscriber());
    }

    public Observable putList(@NonNull PostRequest postRequest) {
        return mRepository.putListDynamically(postRequest.getUrl(), postRequest.getKeyValuePairs(),
                postRequest.getDomainClass(), postRequest.getDataClass(), postRequest.isPersist())
                .map(object -> mModelDataMapper.transformToPresentation(object, postRequest.getPresentationClass()))
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public void executeDynamicDeleteAll(@NonNull Subscriber UseCaseSubscriber, String url, Class dataClass,
                                        boolean persist) {
        subscription = mRepository.deleteAllDynamically(url, dataClass, persist)
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(UseCaseSubscriber);
    }

    /**
     * Executes the current use case.
     *
     * @param postRequest The guy who will be listen to the observable build with .
     */
    @SuppressWarnings("unchecked")
    public void executeDynamicDeleteAll(@NonNull PostRequest postRequest) {
        subscription = mRepository.deleteAllDynamically(postRequest.getUrl(), postRequest.getDataClass(), postRequest.isPersist())
                .compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(postRequest.getSubscriber());
    }

    public Observable deleteAll(@NonNull PostRequest postRequest) {
        return mRepository.deleteAllDynamically(postRequest.getUrl(), postRequest.getDataClass(), postRequest.isPersist())
                .compose(applySchedulers())
                .compose(getLifecycle());
    }

    public <T> void getDataFromJson(@NonNull Subscriber<? super T> subscriber, @NonNull Context context,
                                    String assetName, @NonNull Class<T> dataClass) {
        Observable<T> fetchFromAssets = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(@NonNull Subscriber<? super T> subscriber) {
                try {
                    String data = readFromFile(assetName, context);
                    Gson gson = new Gson();
                    subscriber.onNext(gson.fromJson(data, dataClass));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
        subscription = fetchFromAssets.compose(applySchedulers())
                .compose(getLifecycle())
                .subscribe(subscriber);
    }

    private String readFromFile(String fileName, @NonNull Context context) throws Exception {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null)
                returnString.append(line);
        } finally {
            if (isr != null)
                isr.close();
            if (fIn != null)
                fIn.close();
            if (input != null)
                input.close();
        }
        return returnString.toString();
    }

    /**
     * Unsubscribes from current {@link Subscription}.
     */
    public void unsubscribe() {
        Utils.unsubscribeIfNotNull(subscription);
    }
    //--------------------------------------------------------------------------------//

    /**
     * Apply the default android schedulers to a observable
     *
     * @param <T> the current observable
     * @return the transformed observable
     */
    @NonNull
    protected <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())
                .unsubscribeOn(Schedulers.from(threadExecutor));
    }

    private Observable.Transformer mLifecycle;

    protected void setLifecycle(Observable.Transformer mLifecycle) {
        this.mLifecycle = mLifecycle;
    }

    @NonNull
    protected <T> Observable.Transformer<T, T> getLifecycle() {
        return mLifecycle != null ? mLifecycle : o -> o;
    }
}