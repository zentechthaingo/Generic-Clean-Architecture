package com.zeyad.cleanarchetecturet.domain.interactor;

import com.zeyad.cleanarchetecturet.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchetecturet.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchetecturet.domain.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class GetUserListTest {

    private GetUserList getUserList;
    @Mock
    private ThreadExecutor mockThreadExecutor;
    @Mock
    private PostExecutionThread mockPostExecutionThread;
    @Mock
    private UserRepository mockUserRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        getUserList = new GetUserList(mockUserRepository, mockThreadExecutor,
                mockPostExecutionThread);
    }

    @Test
    public void testGetUserListUseCaseObservableHappyCase() {
        getUserList.buildUseCaseObservable();
        verify(mockUserRepository).users();
        verifyNoMoreInteractions(mockUserRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }
}