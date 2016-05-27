package com.zeyad.cleanarchitecture.data.repository;

import com.zeyad.cleanarchitecture.data.ApplicationTestCase;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.entities.mapper.EntityMapper;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStore;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStoreFactory;
import com.zeyad.cleanarchitecture.domain.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class DataRepositoryTest extends ApplicationTestCase {

    private static final int FAKE_USER_ID = 123;
    private DataRepository userDataRepository;
    @Mock
    private DataStoreFactory mockUserDataStoreFactory;
    @Mock
    private EntityMapper mockUserEntityDataMapper;
    @Mock
    private DataStore mockUserDataStore;
    @Mock
    private UserRealmModel mockUserEntity;
    @Mock
    private User mockUser;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Class domainClass, dataClass;
    private HashMap<String, Object> map;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        domainClass = User.class;
        dataClass = UserRealmModel.class;
        userDataRepository = new DataRepository(mockUserDataStoreFactory);
        given(mockUserDataStoreFactory.dynamically("", mockUserEntityDataMapper)).willReturn(mockUserDataStore);
        given(mockUserDataStoreFactory.dynamically("", "", -1, mockUserEntityDataMapper, dataClass)).willReturn(mockUserDataStore);
        given(mockUserDataStoreFactory.cloud(mockUserEntityDataMapper)).willReturn(mockUserDataStore);
        given(mockUserDataStoreFactory.disk(mockUserEntityDataMapper)).willReturn(mockUserDataStore);
    }

    @Test
    public void testDynamicListFromCloud() throws Exception {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.dynamicList("", domainClass, dataClass, true)).willReturn(Observable.just(usersList));
        userDataRepository.dynamicList("", domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).cloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).dynamicList("", domainClass, dataClass, true);
    }

    @Test
    public void testDynamicListFromDisk() throws Exception {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.dynamicList("", domainClass, dataClass, true)).willReturn(Observable.just(usersList));
        userDataRepository.dynamicList("", domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).disk(mockUserEntityDataMapper);
        verify(mockUserDataStore).dynamicList("", domainClass, dataClass, true);
    }

    @Test
    public void testGetObjectDynamicallyByIdFromCloud() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
//        given(mockUserDataStore.dynamicObject("", "", FAKE_USER_ID, domainClass, dataClass, true)).willReturn(Observable.just(userRealmModel));
        userDataRepository.getObjectDynamicallyById("", "", FAKE_USER_ID, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).cloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).dynamicObject("", "", FAKE_USER_ID, domainClass, dataClass, true);
    }

    @Test
    public void testGetObjectDynamicallyByIdFromDisk() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
//        given(mockUserDataStore.dynamicObject("", "", FAKE_USER_ID, domainClass, dataClass, true)).willReturn(Observable.just(userRealmModel));
        userDataRepository.getObjectDynamicallyById("", "", FAKE_USER_ID, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).disk(mockUserEntityDataMapper);
        verify(mockUserDataStore).dynamicObject("", "", FAKE_USER_ID, domainClass, dataClass, true);
    }

    @Test
    public void testPostObjectDynamicallyToCloud() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        map = new HashMap<>();
        map.put(UserRealmModel.ID_COLUMN, userRealmModel.getUserId());
        map.put(UserRealmModel.COVER_URL, userRealmModel.getCover_url());
        map.put(UserRealmModel.DESCRIPTION, userRealmModel.getDescription());
        map.put(UserRealmModel.EMAIL, userRealmModel.getEmail());
        map.put(UserRealmModel.FOLLOWERS, userRealmModel.getFollowers());
        map.put(UserRealmModel.FULL_NAME_COLUMN, userRealmModel.getFullName());
//        given(mockUserDataStore.dynamicPostObject("", map, domainClass, dataClass, true)).willReturn(Observable.just(true));
        userDataRepository.postObjectDynamically("", map, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).cloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).dynamicPostObject("", map, domainClass, dataClass, true);
    }

    @Test
    public void testPostObjectDynamicallyToDisk() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        map = new HashMap<>();
        map.put(UserRealmModel.ID_COLUMN, userRealmModel.getUserId());
        map.put(UserRealmModel.COVER_URL, userRealmModel.getCover_url());
        map.put(UserRealmModel.DESCRIPTION, userRealmModel.getDescription());
        map.put(UserRealmModel.EMAIL, userRealmModel.getEmail());
        map.put(UserRealmModel.FOLLOWERS, userRealmModel.getFollowers());
        map.put(UserRealmModel.FULL_NAME_COLUMN, userRealmModel.getFullName());
//        given(mockUserDataStore.putToDisk(map, dataClass)).willReturn(Observable.just(true));
        userDataRepository.postObjectDynamically("", map, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).disk(mockUserEntityDataMapper);
        verify(mockUserDataStore).putToDisk(map, dataClass);
    }

    // search
    @Test
    public void testPostListDynamicallyToCloud() throws Exception {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        map = new HashMap<>();
        given(mockUserDataStore.dynamicPostList("", map, domainClass, dataClass, true)).willReturn(Observable.just(usersList));
        userDataRepository.postListDynamically("", map, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).cloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).dynamicPostList("", map, domainClass, dataClass, true);
    }

    // ??
    @Test
    public void testPostListDynamicallyToDisk() throws Exception {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        map = new HashMap<>();
        given(mockUserDataStore.dynamicPostList("", map, domainClass, dataClass, true)).willReturn(Observable.just(usersList));
        userDataRepository.postListDynamically("", map, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).disk(mockUserEntityDataMapper);
        verify(mockUserDataStore).dynamicPostList("", map, domainClass, dataClass, true);
    }

    @Test
    public void testSearchDisk() throws Exception {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        given(mockUserDataStore.searchDisk("s", UserRealmModel.FULL_NAME_COLUMN, domainClass, dataClass))
                .willReturn(Observable.just(usersList));
        userDataRepository.searchDisk("s", UserRealmModel.FULL_NAME_COLUMN, domainClass, dataClass);
        verify(mockUserDataStoreFactory).disk(mockUserEntityDataMapper);
        verify(mockUserDataStore).searchDisk("s", UserRealmModel.FULL_NAME_COLUMN, domainClass, dataClass);
    }

    @Test
    public void testRealmSearchFromDisk() throws Exception {
        List<UserRealmModel> usersList = new ArrayList<>();
        usersList.add(new UserRealmModel());
        RealmQuery realmQuery = Realm.getDefaultInstance().where(UserRealmModel.class)
                .contains(UserRealmModel.FULL_NAME_COLUMN, "s");
        given(mockUserDataStore.searchDisk(realmQuery, domainClass)).willReturn(Observable.just(usersList));
        userDataRepository.searchDisk(realmQuery, dataClass);
        verify(mockUserDataStoreFactory).disk(mockUserEntityDataMapper);
        verify(mockUserDataStore).searchDisk(realmQuery, domainClass);
    }

    @Test
    public void testDeleteListDynamicallyFromCloud() throws Exception {
        List<Long> usersList = new ArrayList<>();
        usersList.add((long) new UserRealmModel().getUserId());
        map = new HashMap<>();
        map.put(DataStore.IDS, usersList);
//        given(mockUserDataStore.deleteCollectionFromCloud("", map, dataClass, true)).willReturn(Observable.just(true));
        userDataRepository.deleteListDynamically("", map, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).cloud(mockUserEntityDataMapper);
        verify(mockUserDataStore).deleteCollectionFromCloud("", map, dataClass, true);
    }

    @Test
    public void testDeleteListDynamicallyFromDisk() throws Exception {
        List<Long> usersList = new ArrayList<>();
        usersList.add((long) new UserRealmModel().getUserId());
        map = new HashMap<>();
        map.put(DataStore.IDS, usersList);
//        given(mockUserDataStore.deleteCollectionFromDisk(map, dataClass)).willReturn(Observable.just(true));
        userDataRepository.deleteListDynamically("", map, domainClass, dataClass, true);
        verify(mockUserDataStoreFactory).disk(mockUserEntityDataMapper);
        verify(mockUserDataStore).deleteCollectionFromDisk(map, dataClass);
    }
}