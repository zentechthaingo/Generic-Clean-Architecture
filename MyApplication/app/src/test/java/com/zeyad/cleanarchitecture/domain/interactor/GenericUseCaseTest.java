package com.zeyad.cleanarchitecture.domain.interactor;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.repositories.Repository;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

public class GenericUseCaseTest {

    private static final int FAKE_USER_ID = 123;
    private GenericUseCase genericUseCase;
    @Mock
    private ThreadExecutor mockThreadExecutor;
    @Mock
    private PostExecutionThread mockPostExecutionThread;
    @Mock
    private Repository mockRepository;
    private Class presentationClass, domainClass, dataClass;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        genericUseCase = new GenericUseCase(mockRepository, mockThreadExecutor, mockPostExecutionThread);
        presentationClass = UserModel.class;
        domainClass = User.class;
        dataClass = UserRealmModel.class;
    }

    @Test
    public void testGenericUseCaseObservableList() {
        genericUseCase.buildUseCaseObservableList(presentationClass, domainClass, dataClass);
        verify(mockRepository).collection(presentationClass, domainClass, dataClass);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testGenericUseCaseObservableDetail() {
        genericUseCase.buildUseCaseObservableDetail(FAKE_USER_ID, presentationClass, domainClass, dataClass);
        verify(mockRepository).getById(FAKE_USER_ID, presentationClass, domainClass, dataClass);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testGenericUseCaseObservableSearch() {
        genericUseCase.buildUseCaseObservableQuery("fake name", "full_name", presentationClass, domainClass, dataClass);
        verify(mockRepository).search("fake name", "full_name", presentationClass, domainClass, dataClass);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testGenericUseCaseObservableDelete() {
        genericUseCase.buildUseCaseObservableDeleteMultiple(Collections.singletonList(FAKE_USER_ID),
                domainClass, dataClass);
        verify(mockRepository).deleteCollection(Collections.singletonList(FAKE_USER_ID),
                domainClass, dataClass);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testGenericUseCaseObservablePut() {
        genericUseCase.buildUseCaseObservablePut(new UserModel(), presentationClass, domainClass, dataClass);
        verify(mockRepository).put(new UserModel(), presentationClass, domainClass, dataClass);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }
}