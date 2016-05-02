package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.network.RestApi;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created by ZIaDo on 4/30/16.
 */
public class CloudDataStoreTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 765;
    private CloudDataStore cloudDataStore;
    @Mock
    private RestApi mockRestApi;
    @Mock
    private GeneralRealmManager mockRealmManager;
    @Mock
    private EntityDataMapper mockUserEntityDataMapper;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cloudDataStore = new CloudDataStore(mockRestApi, mockRealmManager, mockUserEntityDataMapper);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCollection() throws Exception {
        cloudDataStore.collection();
        verify(mockRestApi).userCollection();
    }

    @Test
    public void testGetById() throws Exception {
        UserEntity fakeUserEntity = new UserEntity();
        Observable<Object> fakeObservable = Observable.just(fakeUserEntity);
        given(mockRestApi.objectById(FAKE_USER_ID)).willReturn(fakeObservable);
        cloudDataStore.getById(FAKE_USER_ID);
        verify(mockRestApi).objectById(FAKE_USER_ID);
    }

    @Test
    public void testPostToCloud() throws Exception {
        UserEntity fakeUserEntity = new UserEntity();
        Observable<Object> fakeObservable = Observable.just(fakeUserEntity);
        given(mockRestApi.postItem(fakeUserEntity)).willReturn(fakeObservable);
        cloudDataStore.postToCloud(fakeUserEntity);
        verify(mockRestApi).objectById(FAKE_USER_ID);
    }

    @Test
    public void testSearchCloud() throws Exception {
        cloudDataStore.searchCloud();
        verify(mockRestApi).search();
    }

    @Test
    public void testDeleteCollectionFromCloud() throws Exception {
        cloudDataStore.deleteCollectionFromCloud();
        verify(mockRestApi).deleteCollection();
    }

    @Test
    public void testPutToDisk() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        verify(mockRestApi).postItem();
    }

    @Test
    public void testDeleteCollectionFromDisk() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        verify(mockRestApi).deleteCollection();
    }

    @Test
    public void testSearchDisk() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        verify(mockRestApi).search();
    }
}