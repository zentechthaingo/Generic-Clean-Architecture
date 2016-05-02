package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.DiskUserDataStore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private EntityDataMapper entityDataMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        diskDataStore = new DiskDataStore(mockRealmManager, entityDataMapper);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCollection() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        diskDataStore.collection();
    }

    @Test
    public void testGetById() throws Exception {
        diskDataStore.getById(FAKE_USER_ID);
        verify(mockRealmManager).getById(FAKE_USER_ID);
    }

    @Test
    public void testSearchDisk() throws Exception {
        diskDataStore.searchDisk(FAKE_USER_ID);
        verify(mockRealmManager).getWhere(FAKE_USER_ID);
    }

    @Test
    public void testPutToDisk() throws Exception {
        diskDataStore.putToDisk(FAKE_USER_ID);
        verify(mockRealmManager).put(FAKE_USER_ID);
    }

    @Test
    public void testDeleteCollectionFromDisk() throws Exception {
        diskDataStore.deleteCollectionFromDisk(FAKE_USER_ID);
        verify(mockRealmManager).evictCollection(\FAKE_USER_ID);
    }

    @Test
    public void testSearchCloud() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        diskDataStore.searchCloud();
    }

    @Test
    public void testPostToCloud() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        diskDataStore.postToCloud();
    }

    @Test
    public void testDeleteCollectionFromCloud() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        diskDataStore.deleteCollectionFromCloud();
    }
}