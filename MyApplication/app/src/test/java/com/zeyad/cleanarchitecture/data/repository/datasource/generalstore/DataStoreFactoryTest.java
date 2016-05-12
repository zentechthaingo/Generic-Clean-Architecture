package com.zeyad.cleanarchitecture.data.repository.datasource.generalstore;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityDataMapper;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStore;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStoreFactory;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.CloudUserDataStore;
import com.zeyad.cleanarchitecture.data.repository.datasource.userstore.DiskUserDataStore;
import com.zeyad.cleanarchitecture.utilities.Constants;

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

public class DataStoreFactoryTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 11;

    private DataStoreFactory dataStoreFactory;
    @Mock
    private GeneralRealmManager mockRealmManager;
    @Mock
    private EntityDataMapper mockEntityDataMapper;
    @Mock
    private Context mContext;
    private Class dataClass;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        dataStoreFactory = new DataStoreFactory(mockRealmManager, mContext);
        dataClass = UserRealmModel.class;
    }

    @Test
    public void testCreateDiskDataStore() {
        given(mockRealmManager.isCached(FAKE_USER_ID, dataClass)).willReturn(true);
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
                .willReturn(false);

        DataStore userDataStore = dataStoreFactory.createByIdFromDisk(mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskUserDataStore.class)));

        verify(mockRealmManager).isCached(FAKE_USER_ID, dataClass);
        verify(mockRealmManager).isItemValid(FAKE_USER_ID, dataClass);
    }

    @Test
    public void testCreateCloudDataStore() {
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE)).willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID, dataClass)).willReturn(false);

        DataStore userDataStore = dataStoreFactory.createByIdFromCloud(mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudUserDataStore.class)));

        verify(mockRealmManager).isItemValid(FAKE_USER_ID, dataClass);
    }

    @Test
    public void testCreateDiskDataStoreForUsers() {
        given(mockRealmManager.isCached(FAKE_USER_ID, dataClass)).willReturn(true);
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
                .willReturn(false);

        DataStore dataStore = dataStoreFactory.createAllFromDisk(mockEntityDataMapper);

        assertThat(dataStore, is(notNullValue()));
        assertThat(dataStore, is(instanceOf(DiskUserDataStore.class)));

        verify(mockRealmManager).areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE);
    }

    @Test
    public void testCreateCloudDataStoreForUsers() {
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
                .willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID, dataClass)).willReturn(false);

        DataStore userDataStore = dataStoreFactory.createAllFromCloud(mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudUserDataStore.class)));

        verify(mockRealmManager).areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE);
    }
}