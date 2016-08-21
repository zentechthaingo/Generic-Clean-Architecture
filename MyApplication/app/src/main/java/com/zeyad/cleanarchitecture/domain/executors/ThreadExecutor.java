package com.zeyad.cleanarchitecture.domain.executors;

import java.util.concurrent.Executor;

/**
 * Executor implementation can be based on different frameworks or techniques of asynchronous
 * execution, but every implementation will executeGetObject the
 * {@link com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase} out of the UI thread.
 */
public interface ThreadExecutor extends Executor {
}