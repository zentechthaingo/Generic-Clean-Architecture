package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.generalize.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.CloudUserDataStore;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.DiskUserDataStore;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStore;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.UserDataStoreFactory;

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

/**
 * Created by ZIaDo on 4/30/16.
 */
public class DataStoreFactoryTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 11;

    private DataStoreFactory dataStoreFactory;
    @Mock
    private GeneralRealmManager mockRealmManager;
    @Mock
    private EntityDataMapper mockEntityDataMapper;
    @Mock
    private Context mContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dataStoreFactory = new DataStoreFactory(mockRealmManager, mContext);
    }

    @Test
    public void testCreateDiskDataStore() {
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(true);
        given(mockRealmManager.areItemsValid()).willReturn(false);

        UserDataStore userDataStore = dataStoreFactory.createById(FAKE_USER_ID, mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskUserDataStore.class)));

        verify(mockRealmManager).isCached(FAKE_USER_ID);
        verify(mockRealmManager).isUserValid(FAKE_USER_ID);
    }

    @Test
    public void testCreateCloudDataStore() {
        given(mockRealmManager.areUsersValid()).willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(false);

        UserDataStore userDataStore = dataStoreFactory.createById(FAKE_USER_ID, mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudUserDataStore.class)));

        verify(mockRealmManager).isUserValid(FAKE_USER_ID);
    }

    @Test
    public void testCreateDiskDataStoreForUsers() {
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(true);
        given(mockRealmManager.areItemsValid()).willReturn(false);

        DataStore dataStore = dataStoreFactory.createAllFromDisk(mockEntityDataMapper);

        assertThat(dataStore, is(notNullValue()));
        assertThat(dataStore, is(instanceOf(DiskUserDataStore.class)));

        verify(mockRealmManager).areItemsValid();
    }

    @Test
    public void testCreateCloudDataStoreForUsers() {
        given(mockRealmManager.areItemsValid()).willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(false);

        DataStore userDataStore = dataStoreFactory.createAllFromCloud(mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudUserDataStore.class)));

        verify(mockRealmManager).areItemsValid();
    }
}