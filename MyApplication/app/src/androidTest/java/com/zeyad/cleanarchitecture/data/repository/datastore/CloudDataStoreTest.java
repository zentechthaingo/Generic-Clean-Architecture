package com.zeyad.cleanarchitecture.data.repository.datastore;

import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.network.RestApi;
import com.zeyad.cleanarchitecture.data.repository.generalstore.CloudDataStore;
import com.zeyad.cleanarchitecture.data.repository.generalstore.DataStore;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.utilities.Constants;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created by ZIaDo on 4/30/16.
 */
public class CloudDataStoreTest extends AndroidTestCase {

    private static final int FAKE_USER_ID = 765;
    private CloudDataStore cloudDataStore;
    @Mock
    private RestApi mockRestApi;
    @Mock
    private GeneralRealmManager mockRealmManager;
    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;
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
        cloudDataStore.dynamicList(Constants.API_BASE_URL + "users.json", domainClass, dataClass, true);
        verify(mockRestApi).userCollection();
    }

    @Test
    public void testGetById() throws Exception {
        UserRealmModel fakeUserEntity = new UserRealmModel();
        Observable<Object> fakeObservable = Observable.just(fakeUserEntity);
        given(mockRestApi.objectById(FAKE_USER_ID)).willReturn(fakeObservable);
        cloudDataStore.dynamicObject("", "", FAKE_USER_ID, domainClass, dataClass, false);
        verify(mockRestApi).objectById(FAKE_USER_ID);
    }

    @Test
    public void testPostToCloud() throws Exception {
        UserRealmModel fakeUserEntity = new UserRealmModel();
        Observable<Object> fakeObservable = Observable.just(fakeUserEntity);
        given(mockRestApi.postItem(fakeUserEntity)).willReturn(fakeObservable);
        HashMap map = new HashMap();
        cloudDataStore.dynamicPostObject("", map, domainClass, dataClass, true);
        verify(mockRestApi).objectById(FAKE_USER_ID);
    }

    @Test
    public void testSearchCloud() throws Exception {
        cloudDataStore.dynamicList("", domainClass, dataClass, false);
        verify(mockRestApi).search("");
    }

    @Test
    public void testDeleteCollectionFromCloud() throws Exception {
        List<Integer> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel().getUserId());
        HashMap map = new HashMap();
        map.put(DataStore.IDS, usersList);
        cloudDataStore.deleteCollectionFromCloud("", map, dataClass, true);
        verify(mockRestApi).deleteCollection(usersList);
    }

    @Test
    public void testPutToDisk() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        verify(mockRestApi).postItem(new UserRealmModel());
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