package com.zeyad.cleanarchitecturet.data.exception;

import com.zeyad.cleanarchitecturet.data.ApplicationTestCase;
import com.zeyad.cleanarchitecturet.data.exceptions.RepositoryErrorBundle;

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