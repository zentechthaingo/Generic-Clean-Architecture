package com.zeyad.cleanarchitecture.data.repository.datasource.userstore;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.RealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.network.RestApi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class CloudUserDataStoreTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 765;
    private CloudUserDataStore cloudUserDataStore;
    @Mock
    private RestApi mockRestApi;
    @Mock
    private RealmManager mockRealmManager;
    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cloudUserDataStore = new CloudUserDataStore(mockRestApi, mockRealmManager, mockUserEntityDataMapper);
    }

    @Test
    public void testGetUserEntityListFromApi() {
        cloudUserDataStore.userEntityList();
        verify(mockRestApi).userCollection();
    }

    @Test
    public void testGetUserEntityDetailsFromApi() {
        UserEntity fakeUserEntity = new UserEntity();
        Observable<Object> fakeObservable = Observable.just(fakeUserEntity);
        given(mockRestApi.objectById(FAKE_USER_ID)).willReturn(fakeObservable);
        cloudUserDataStore.userEntityDetails(FAKE_USER_ID);
        verify(mockRestApi).objectById(FAKE_USER_ID);
    }
}