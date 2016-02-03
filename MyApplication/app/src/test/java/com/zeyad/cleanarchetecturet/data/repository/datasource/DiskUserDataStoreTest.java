package com.zeyad.cleanarchetecturet.data.repository.datasource;

import com.zeyad.cleanarchetecturet.data.ApplicationTestCase;
import com.zeyad.cleanarchetecturet.data.db.RealmManager;
import com.zeyad.cleanarchetecturet.data.entities.mapper.UserEntityDataMapper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class DiskUserDataStoreTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 11;
    private DiskUserDataStore diskUserDataStore;
    @Mock
    private RealmManager mockRealmManager;
    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        diskUserDataStore = new DiskUserDataStore(mockRealmManager, mockUserEntityDataMapper);
    }

    @Test
    public void testGetUserEntityListUnsupported() {
        expectedException.expect(UnsupportedOperationException.class);
        diskUserDataStore.userEntityList();
    }

    @Test
    public void testGetUserEntityDetailesFromCache() {
        diskUserDataStore.userEntityDetails(FAKE_USER_ID);
        verify(mockRealmManager).get(FAKE_USER_ID);
    }
}