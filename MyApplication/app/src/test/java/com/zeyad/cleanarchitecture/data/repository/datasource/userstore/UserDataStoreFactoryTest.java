package com.zeyad.cleanarchitecture.data.repository.datasource.userstore;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.db.RealmManager;

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

// TODO: 3/2/16 Update!
public class UserDataStoreFactoryTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 11;

    private UserDataStoreFactory userDataStoreFactory;
    @Mock
    private RealmManager mockRealmManager;
    @Mock
    private UserEntityDataMapper mockUserEntityDataMapper;
    @Mock
    private Context mContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userDataStoreFactory =
                new UserDataStoreFactory(mockRealmManager, mContext);
    }

    @Test
    public void testCreateDiskDataStore() {
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(true);
        given(mockRealmManager.areUsersValid()).willReturn(false);

        UserDataStore userDataStore = userDataStoreFactory.createById(FAKE_USER_ID, mockUserEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskUserDataStore.class)));

        verify(mockRealmManager).isCached(FAKE_USER_ID);
        verify(mockRealmManager).isUserValid(FAKE_USER_ID);
    }

    @Test
    public void testCreateCloudDataStore() {
        given(mockRealmManager.areUsersValid()).willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(false);

        UserDataStore userDataStore = userDataStoreFactory.createById(FAKE_USER_ID, mockUserEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudUserDataStore.class)));

        verify(mockRealmManager).isUserValid(FAKE_USER_ID);
    }

    @Test
    public void testCreateDiskDataStoreForUsers() {
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(true);
        given(mockRealmManager.areUsersValid()).willReturn(false);

        UserDataStore userDataStore = userDataStoreFactory.createAll(mockUserEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(DiskUserDataStore.class)));

        verify(mockRealmManager).areUsersValid();
    }

    @Test
    public void testCreateCloudDataStoreForUsers() {
        given(mockRealmManager.areUsersValid()).willReturn(true);
        given(mockRealmManager.isCached(FAKE_USER_ID)).willReturn(false);

        UserDataStore userDataStore = userDataStoreFactory.createAll(mockUserEntityDataMapper);

        assertThat(userDataStore, is(notNullValue()));
        assertThat(userDataStore, is(instanceOf(CloudUserDataStore.class)));

        verify(mockRealmManager).areUsersValid();
    }
}
