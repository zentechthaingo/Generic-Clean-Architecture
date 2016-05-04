package com.zeyad.cleanarchitecture.data.repository;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.generalstore.DataStore;
import com.zeyad.cleanarchitecture.data.repository.datasource.generalstore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;

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
    private EntityDataMapper mockUserEntityDataMapper;
    @Mock
    private DataStore mockUserDataStore;
    @Mock
    private UserEntity mockUserEntity;
    @Mock
    private User mockUser;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Class presentationClass, domainClass, dataClass;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presentationClass = UserModel.class;
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
        List<UserEntity> usersList = new ArrayList<>();
        usersList.add(new UserEntity());
        given(mockUserDataStore.collection(domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.collection(presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createAllFromCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).collection(domainClass, dataClass);
    }

    @Test
    public void testGetUsersFromDisk() {
        List<UserEntity> usersList = new ArrayList<>();
        usersList.add(new UserEntity());
        given(mockUserDataStore.collection(domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.collection(presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createAllFromDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).collection(domainClass, dataClass);
    }

    @Test
    public void testGetUserFromCloud() {
        UserEntity userEntity = new UserEntity();
        given(mockUserDataStore.getById(FAKE_USER_ID, domainClass, dataClass)).willReturn(Observable.just(userEntity));
        userDataRepository.getById(FAKE_USER_ID, presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createByIdFromCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).getById(FAKE_USER_ID, domainClass, dataClass);
    }

    @Test
    public void testGetUserFromDisk() {
        UserEntity userEntity = new UserEntity();
        given(mockUserDataStore.getById(FAKE_USER_ID, domainClass, dataClass)).willReturn(Observable.just(userEntity));
        userDataRepository.getById(FAKE_USER_ID, presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).createByIdFromDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).getById(FAKE_USER_ID, domainClass, dataClass);
    }

    @Test
    public void testSearchUsersFromCloud() {
        List<UserEntity> usersList = new ArrayList<>();
        usersList.add(new UserEntity());
        given(mockUserDataStore.searchCloud("", domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.search("", "", presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).searchCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).searchCloud("", domainClass, dataClass);
    }

    @Test
    public void testSearchUsersFromDisk() {
        List<UserEntity> usersList = new ArrayList<>();
        usersList.add(new UserEntity());
        given(mockUserDataStore.searchDisk("", "", domainClass, dataClass)).willReturn(Observable.just(usersList));
        userDataRepository.search("", "", presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).searchDisk(mockUserEntityDataMapper);
        verify(mockUserDataStore).searchDisk("", "", domainClass, dataClass);
    }

    @Test
    public void testPutUserFromCloud() {
        UserEntity userEntity = new UserEntity();
        given(mockUserDataStore.postToCloud(userEntity, domainClass, dataClass)).willReturn(Observable.just(true));
        userDataRepository.put(userEntity, presentationClass, domainClass, dataClass);
        verify(mockUserDataStoreFactory).putToCloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).postToCloud(userEntity, domainClass, dataClass);
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
        List<UserEntity> usersList = new ArrayList<>();
        usersList.add(new UserEntity());
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