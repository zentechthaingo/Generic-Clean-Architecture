package com.zeyad.cleanarchitecture.data.exceptions;

import com.zeyad.cleanarchitecture.domain.exceptions.ErrorBundle;

/**
 * Wrapper around Exceptions used to manage errors in the repository.
 */
public class RepositoryErrorBundle implements ErrorBundle {

    private final Exception exception;

    public RepositoryErrorBundle(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public String getErrorMessage() {
        if (exception != null)
            return exception.getMessage();
        return "";
    }
}