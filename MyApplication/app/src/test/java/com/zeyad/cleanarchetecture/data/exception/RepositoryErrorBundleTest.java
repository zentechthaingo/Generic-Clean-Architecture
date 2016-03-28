package com.zeyad.cleanarchetecture.data.exception;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.exceptions.RepositoryErrorBundle;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class RepositoryErrorBundleTest extends ApplicationTestCase {

    private RepositoryErrorBundle repositoryErrorBundle;

    @Mock
    private Exception mockException;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        repositoryErrorBundle = new RepositoryErrorBundle(mockException);
    }

    @Test
    public void testGetErrorMessageInteraction() {
        repositoryErrorBundle.getErrorMessage();

        verify(mockException).getMessage();
    }
}