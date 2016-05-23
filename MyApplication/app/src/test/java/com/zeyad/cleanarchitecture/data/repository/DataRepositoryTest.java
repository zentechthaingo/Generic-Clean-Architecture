package com.zeyad.cleanarchitecture.data.repository;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStore;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class DataRepositoryTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 123;
    private DataRepository userDataRepository;
    @Mock
    private DataStoreFactory mockUserDataStoreFactory;
    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;
    @Mock
    private DataStore mockUserDataStore;
    @Mock
    private UserRealmModel mockUserEntity;
    @Mock
    private User mockUser;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Class presentationClass, domainClass, dataClass;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presentationClass = UserViewModel.class;
        domainClass = User.class;
        dataClass = UserRealmModel.class;
        userDataRepository = new DataRepository(mockUserDataStoreFactory, mockUserEntityDataMapper);
        given(mockUserDataStoreFactory.createByIdFromCloud(mockUserEntityDataMapper)).willReturn(mockUserDataStore);
        given(mockUserDataStoreFactory.createByIdFromDisk(mockUserEntityDataMapper)).willReturn(mockUserDataStore);
        given(mockUserDataStoreFactory.createAllFromCloud(mockUserEntityDataMapper)).willReturn(mockUserDataStore);
        given(mockUserDataStoreFactory.createAllFromDisk(mockUserEntityDataMapper)).willReturn(mockUserDataStore);
    }

    @Test
    public void testGetUsersFromCloud() {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.collection(domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.collection(presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createAllFromCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).collection(domainClass, dataClass);
    }

    @Test
    public void testGetUsersFromDisk() {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.collection(domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.collection(presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createAllFromDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).collection(domainClass, dataClass);
    }

    @Test
    public void testGetUserFromCloud() {
        UserRealmModel UserRealmModel = new UserRealmModel();
        given(mockUserDataStore.getById(FAKE_USER_ID, domainClass, dataClass)).willReturn(Observable.just(UserRealmModel));
        userDataRepository.getById(FAKE_USER_ID, presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createByIdFromCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).getById(FAKE_USER_ID, domainClass, dataClass);
    }

    @Test
    public void testGetUserFromDisk() {
        UserRealmModel UserRealmModel = new UserRealmModel();
        given(mockUserDataStore.getById(FAKE_USER_ID, domainClass, dataClass)).willReturn(Observable.just(UserRealmModel));
        userDataRepository.getById(FAKE_USER_ID, presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createByIdFromDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).getById(FAKE_USER_ID, domainClass, dataClass);
    }

    @Test
    public void testSearchUsersFromCloud() {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.searchCloud("", domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.search("", "", presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).searchCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).searchCloud("", domainClass, dataClass);
    }

    @Test
    public void testSearchUsersFromDisk() {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.searchDisk("", "", domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.search("", "", presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).searchDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).searchDisk("", "", domainClass, dataClass);
    }

    @Test
    public void testPutUserFromCloud() {
        UserRealmModel UserRealmModel = new UserRealmModel();
        given(mockUserDataStore.postToCloud(UserRealmModel, domainClass, dataClass)).willReturn(Observable.just(true));
        userDataRepository.put(UserRealmModel, presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).putToCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).postToCloud(UserRealmModel, domainClass, dataClass);
    }

    @Test
    public void testPutUserFromDisk() {
        UserRealmModel userRealmModel = new UserRealmModel();
        given(mockUserDataStore.putToDisk(userRealmModel)).willReturn(Observable.just(true));
        userDataRepository.put(userRealmModel, presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).putToDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).putToDisk(userRealmModel);
    }

    @Test
    public void testDeleteUsersFromCloud() {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.deleteCollectionFromCloud(usersList, domainClass, dataClass)).willReturn(Observable.just(true));
        userDataRepository.deleteCollection(usersList, domainClass, dataClass);
        verify(mockUserDataStoreFactory).deleteCollectionFromCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).deleteCollectionFromCloud(usersList, domainClass, dataClass);
    }

    @Test
    public void testDeleteUsersFromDisk() {
        List<Integer> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel().getUserId());
        given(mockUserDataStore.deleteCollectionFromDisk(usersList, dataClass)).willReturn(Observable.just(true));
        userDataRepository.deleteCollection(usersList, domainClass, dataClass);
        verify(mockUserDataStoreFactory).deleteCollectionFromDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).deleteCollectionFromDisk(usersList, dataClass);
    }
}