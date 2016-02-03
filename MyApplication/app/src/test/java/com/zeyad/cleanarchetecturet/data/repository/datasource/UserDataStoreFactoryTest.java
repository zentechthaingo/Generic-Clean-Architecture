package com.zeyad.cleanarchetecturet.data.repository.datasource;

import com.zeyad.cleanarchetecturet.data.ApplicationTestCase;
import com.zeyad.cleanarchetecturet.data.db.RealmManager;
import com.zeyad.cleanarchetecturet.data.entities.mapper.UserEntityDataMapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class UserDataStoreFactoryTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 11;

    private UserDataStoreFactory userDataStoreFactory;
    @Mock
    private RealmManager mockRealmManager;
    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDataStoreFactory =
                new UserDataStoreFactory(mockRealmManager);
    }

    @Test
    public void testCreateDiskDataStore() {
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(true);
        given(mockRealmManager.isValid()).willReturn(false);

        UserDataStore userDataStore = userDataStoreFactory.createById(FAKE_USER_ID, mockUserEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskUserDataStore.class)));

        verify(mockRealmManager).isCached(FAKE_USER_ID);
        verify(mockRealmManager).isValid();
    }

    @Test
    public void testCreateCloudDataStore() {
        given(mockRealmManager.isValid()).willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(false);

        UserDataStore userDataStore = userDataStoreFactory.createById(FAKE_USER_ID, mockUserEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudUserDataStore.class)));

        verify(mockRealmManager).isValid();
    }
}
