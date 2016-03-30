package com.zeyad.cleanarchitecture.domain.executors;

import com.zeyad.cleanarchitecture.domain.interactor.BaseUseCase;

import java.util.concurrent.Executor;

/**
 * Executor implementation can be based on different frameworks or techniques of asynchronous
 * execution, but every implementation will executeDetail the
 * {@link BaseUseCase} out of the UI thread.
 */
public interface ThreadExecutor extends Executor {
}