package com.zeyad.cleanarchitecturet.domain.interactor;

import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
 * By convention each UseCase implementation will return the result using a {@link rx.Subscriber}
 * that will execute its job in a background thread and will post the result in the UI thread.
 */
public abstract class UseCase {

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;

    private Subscription subscription;

    protected UseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        subscription = Subscriptions.empty();
    }

    /**
     * Builds an {@link rx.Observable} which will be used when executing the current {@link UseCase}.
     */
    protected abstract Observable buildUseCaseObservable();

    /**
     * Executes the current use case.
     *
     * @param UseCaseSubscriber The guy who will be listen to the observable build with {@link #buildUseCaseObservable()}.
     */
    @SuppressWarnings("unchecked")
    public void execute(Subscriber UseCaseSubscriber) {
        final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
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
//                    .groupBy(o -> new AtomicInteger(0).getAndIncrement() % Constants.THREADCT)
                .subscribeOn(Schedulers.from(executor))
                .observeOn(postExecutionThread.getScheduler())
//                    .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(executor::shutdown
                ).subscribe(UseCaseSubscriber);
    }

    /**
     * Unsubscribes from current {@link rx.Subscription}.
     */
    public void unsubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}