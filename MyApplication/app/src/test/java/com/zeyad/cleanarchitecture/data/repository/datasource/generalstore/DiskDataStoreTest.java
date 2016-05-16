package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datastore.DiskDataStore;
import com.zeyad.cleanarchitecture.domain.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * Created by ZIaDo on 4/30/16.
 */
public class DiskDataStoreTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 11;
    private DiskDataStore diskDataStore;
    @Mock
    private GeneralRealmManager mockRealmManager;
    @Mock
    private UserEntityDataMapper entityDataMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Class domainClass, dataClass;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        diskDataStore = new DiskDataStore(mockRealmManager, entityDataMapper);
        domainClass = User.class;
        dataClass = UserRealmModel.class;

    }

    @Test
    public void testCollection() throws Exception {
        diskDataStore.collection(domainClass, dataClass);
        verify(mockRealmManager).getAll(dataClass);
    }

    @Test
    public void testGetById() throws Exception {
        diskDataStore.getById(FAKE_USER_ID, domainClass, dataClass);
        verify(mockRealmManager).getById(FAKE_USER_ID, dataClass);
    }

    @Test
    public void testSearchDisk() throws Exception {
        diskDataStore.searchDisk("", "", domainClass, dataClass);
        verify(mockRealmManager).getWhere(dataClass, "", "");
    }

    @Test
    public void testPutToDisk() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        diskDataStore.putToDisk(userRealmModel);
        verify(mockRealmManager).put(userRealmModel);
    }

    @Test
    public void testDeleteCollectionFromDisk() throws Exception {
        List<Integer> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel().getUserId());
        diskDataStore.deleteCollectionFromDisk(usersList, dataClass);
        verify(mockRealmManager).evictCollection(usersList, dataClass);
    }

    @Test
    public void testSearchCloud() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        diskDataStore.searchCloud("", domainClass, dataClass);
    }

    @Test
    public void testPostToCloud() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        diskDataStore.postToCloud(new UserRealmModel(), domainClass, dataClass);
    }

    @Test
    public void testDeleteCollectionFromCloud() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        diskDataStore.deleteCollectionFromCloud(new ArrayList<>(), domainClass, dataClass);
    }
}