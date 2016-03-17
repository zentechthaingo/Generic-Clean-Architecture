package com.zeyad.cleanarchitecturet.domain.executors;

import com.zeyad.cleanarchitecturet.domain.interactor.BaseUseCase;

import java.util.concurrent.Executor;

/**
 * Executor implementation can be based on different frameworks or techniques of asynchronous
 * execution, but every implementation will execute the
 * {@link BaseUseCase} out of the UI thread.
 */
public interface ThreadExecutor extends Executor {
}