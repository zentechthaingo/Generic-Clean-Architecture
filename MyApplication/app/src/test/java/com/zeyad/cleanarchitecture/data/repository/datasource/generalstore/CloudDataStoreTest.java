package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.network.RestApi;
import com.zeyad.cleanarchitecture.data.repository.datastore.CloudDataStore;
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
    private Class domainClass, dataClass;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cloudDataStore = new CloudDataStore(mockRestApi, mockRealmManager, mockUserEntityDataMapper);
        domainClass = User.class;
        dataClass = UserRealmModel.class;
    }

    @Test
    public void testCollection() throws Exception {
        cloudDataStore.collection(domainClass, dataClass);
        verify(mockRestApi).userCollection();
    }

    @Test
    public void testGetById() throws Exception {
        UserEntity fakeUserEntity = new UserEntity();
        Observable<Object> fakeObservable = Observable.just(fakeUserEntity);
        given(mockRestApi.objectById(FAKE_USER_ID)).willReturn(fakeObservable);
        cloudDataStore.getById(FAKE_USER_ID, domainClass, dataClass);
        verify(mockRestApi).objectById(FAKE_USER_ID);
    }

    @Test
    public void testPostToCloud() throws Exception {
        UserEntity fakeUserEntity = new UserEntity();
        Observable<Object> fakeObservable = Observable.just(fakeUserEntity);
        given(mockRestApi.postItem(fakeUserEntity)).willReturn(fakeObservable);
        cloudDataStore.postToCloud(fakeUserEntity, domainClass, dataClass);
        verify(mockRestApi).objectById(FAKE_USER_ID);
    }

    @Test
    public void testSearchCloud() throws Exception {
        cloudDataStore.searchCloud("", domainClass, dataClass);
        verify(mockRestApi).search("");
    }

    @Test
    public void testDeleteCollectionFromCloud() throws Exception {
        List<Integer> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel().getUserId());
        cloudDataStore.deleteCollectionFromCloud(usersList, domainClass, dataClass);
        verify(mockRestApi).deleteCollection(usersList);
    }

    @Test
    public void testPutToDisk() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        verify(mockRestApi).postItem(new UserEntity());
    }

    @Test
    public void testDeleteCollectionFromDisk() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        verify(mockRestApi).deleteCollection(new ArrayList<>());
    }

    @Test
    public void testSearchDisk() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        verify(mockRestApi).search("");
    }
}