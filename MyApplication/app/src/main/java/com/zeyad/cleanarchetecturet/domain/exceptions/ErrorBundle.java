package com.zeyad.cleanarchetecturet.domain.exceptions;

/**
 * Interface to represent a wrapper around an {@link Exception} to manage errors.
 */
public interface ErrorBundle {
    Exception getException();

    String getErrorMessage();
}