package com.zeyad.cleanarchitecture.data.repository;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStoreFactory;
import com.zeyad.cleanarchitecture.domain.models.User;

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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

public class UserDataRepositoryTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 123;
    private UserDataRepository userDataRepository;
    @Mock
    private UserDataStoreFactory mockUserDataStoreFactory;
    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;
    @Mock
    private UserDataStore mockUserDataStore;
    @Mock
    private UserEntity mockUserEntity;
    @Mock
    private User mockUser;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDataRepository = new UserDataRepository(mockUserDataStoreFactory,
                mockUserEntityDataMapper);
        given(mockUserDataStoreFactory.createById(anyInt(), mockUserEntityDataMapper)).willReturn(mockUserDataStore);
        given(mockUserDataStoreFactory.createCloudDataStore(mockUserEntityDataMapper)).willReturn(mockUserDataStore);
    }

    @Test
    public void testGetUsersHappyCase() {
        List<UserEntity> usersList = new ArrayList<>();
        usersList.add(new UserEntity());
        given(mockUserDataStore.userEntityList()).willReturn(Observable.just(usersList));
        userDataRepository.users();
        verify(mockUserDataStoreFactory).createCloudDataStore(mockUserEntityDataMapper);
        verify(mockUserDataStore).userEntityList();
    }

    @Test
    public void testGetUserHappyCase() {
        UserEntity userEntity = new UserEntity();
        given(mockUserDataStore.userEntityDetails(FAKE_USER_ID)).willReturn(Observable.just(userEntity));
        userDataRepository.user(FAKE_USER_ID);

        verify(mockUserDataStoreFactory).createById(FAKE_USER_ID, mockUserEntityDataMapper);
        verify(mockUserDataStore).userEntityDetails(FAKE_USER_ID);
    }
}