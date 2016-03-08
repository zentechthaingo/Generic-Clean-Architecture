package com.zeyad.cleanarchitecturet.domain.interactor;

import com.zeyad.cleanarchitecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecturet.domain.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class GetUserDetailsTest {

    private static final int FAKE_USER_ID = 123;
    private GetUserDetails getUserDetails;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ThreadExecutor mockThreadExecutor;
    @Mock
    private PostExecutionThread mockPostExecutionThread;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        getUserDetails = new GetUserDetails(FAKE_USER_ID, mockUserRepository,
                mockThreadExecutor, mockPostExecutionThread);
    }

    @Test
    public void testGetUserDetailsUseCaseObservableHappyCase() {
        getUserDetails.buildUseCaseObservable();

        verify(mockUserRepository).user(FAKE_USER_ID);
        verifyNoMoreInteractions(mockUserRepository);
        verifyZeroInteractions(mockPostExecutionThread);
        verifyZeroInteractions(mockThreadExecutor);
    }
}