package com.zeyad.cleanarchitecture.domain.interactors;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.executors.PostExecutionThread;
import com.zeyad.cleanarchitecture.domain.executors.ThreadExecutor;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.domain.repository.Repository;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by ZIaDo on 5/22/16.
 */
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
        presentationClass = UserViewModel.class;
        domainClass = User.class;
        dataClass = UserRealmModel.class;
    }

    @Test
    public void testBuildUseCaseObservableDynamicList() throws Exception {
        genericUseCase.buildUseCaseObservableDynamicList("", presentationClass, domainClass,
                dataClass, true);
        verify(mockRepository).dynamicList("", presentationClass, domainClass, true);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testBuildUseCaseObservableDynamicObjectById() throws Exception {
        genericUseCase.buildUseCaseObservableDynamicObjectById("", "", FAKE_USER_ID, presentationClass,
                domainClass, dataClass, false);
        verify(mockRepository).getObjectDynamicallyById("", "", FAKE_USER_ID, domainClass,
                dataClass, false);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testBuildUseCaseObservablePut() throws Exception {
        HashMap map = new HashMap();
        map.put("", new UserViewModel());
        genericUseCase.buildUseCaseObservablePut("", map, presentationClass, domainClass, dataClass, false);
        verify(mockRepository).postObjectDynamically("", map, domainClass, dataClass, false);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testBuildUseCaseObservableDynamicPostList() throws Exception {
        HashMap map = new HashMap();
        map.put("", new UserViewModel());
        genericUseCase.buildUseCaseObservableDynamicPostList("", map, presentationClass, domainClass,
                dataClass, false);
        verify(mockRepository).postListDynamically("", map, domainClass, dataClass, false);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testBuildUseCaseObservableDeleteMultiple() throws Exception {
        HashMap map = new HashMap();
        map.put("", Collections.singletonList(FAKE_USER_ID));
        genericUseCase.buildUseCaseObservableDeleteMultiple("", map, domainClass, dataClass, true);
        verify(mockRepository).deleteListDynamically("", map, domainClass, dataClass, true);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testBuildUseCaseObservableQuery() throws Exception {
        genericUseCase.buildUseCaseObservableQuery("fake name", "full_name", presentationClass, domainClass, dataClass);
        verify(mockRepository).searchDisk("fake name", "full_name", domainClass, dataClass);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }

    @Test
    public void testBuildUseCaseObservableRealmQuery() throws Exception {
        RealmQuery query = Realm.getDefaultInstance().where(UserRealmModel.class).contains("full_name", "fake name");
        genericUseCase.buildUseCaseObservableRealmQuery(query, presentationClass, domainClass);
        verify(mockRepository).searchDisk(query, domainClass);
        verifyNoMoreInteractions(mockRepository);
        verifyZeroInteractions(mockThreadExecutor);
        verifyZeroInteractions(mockPostExecutionThread);
    }
}