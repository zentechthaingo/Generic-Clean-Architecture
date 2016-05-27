package com.zeyad.cleanarchitecture.data.repository.datastore;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.db.GeneralRealmManager;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.UserEntityDataMapper;
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

/**
 * @author ZIaDo on 5/22/16.
 */
public class DataStoreFactoryTest {

    private static final int FAKE_USER_ID = 11;

    private DataStoreFactory dataStoreFactory;
    @Mock
    private GeneralRealmManager mockRealmManager;
    @Mock
    private UserEntityDataMapper mockEntityDataMapper;
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
    public void testListDynamically() throws Exception {
        given(mockRealmManager.isCached(FAKE_USER_ID, "", dataClass)).willReturn(true);
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
                .willReturn(false);

        DataStore userDataStore = dataStoreFactory.dynamically("", mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskDataStore.class)));

        verify(mockRealmManager).isCached(FAKE_USER_ID, "", dataClass);
        verify(mockRealmManager).isItemValid(FAKE_USER_ID, "", dataClass);
    }

    @Test
    public void testObjectDynamically1() throws Exception {
        given(mockRealmManager.isCached(FAKE_USER_ID, "", dataClass)).willReturn(true);
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
                .willReturn(false);

        DataStore userDataStore = dataStoreFactory.dynamically("", "", -1, mockEntityDataMapper,
                UserRealmModel.class);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskDataStore.class)));

        verify(mockRealmManager).isCached(FAKE_USER_ID, "", dataClass);
        verify(mockRealmManager).isItemValid(FAKE_USER_ID, "", dataClass);
    }

    @Test
    public void testDisk() throws Exception {
        given(mockRealmManager.isCached(FAKE_USER_ID, "", dataClass)).willReturn(true);
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE))
                .willReturn(false);

        DataStore userDataStore = dataStoreFactory.disk(mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskDataStore.class)));

        verify(mockRealmManager).isCached(FAKE_USER_ID, "", dataClass);
        verify(mockRealmManager).isItemValid(FAKE_USER_ID, "", dataClass);
    }

    @Test
    public void testCloud() throws Exception {
        given(mockRealmManager.areItemsValid(Constants.COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE)).willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID, "", dataClass)).willReturn(false);

        DataStore userDataStore = dataStoreFactory.cloud(mockEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudDataStore.class)));

        verify(mockRealmManager).isItemValid(FAKE_USER_ID, "", dataClass);
    }
}