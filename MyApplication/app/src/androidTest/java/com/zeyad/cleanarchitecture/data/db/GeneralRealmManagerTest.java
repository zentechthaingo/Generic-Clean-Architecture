package com.zeyad.cleanarchitecture.data.db;

import android.content.Context;
import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.utilities.Constants;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufs.github.rxassertions.RxAssertions;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import rx.Observable;
import rx.Subscriber;

import static org.mockito.Mockito.verify;

public class GeneralRealmManagerTest extends AndroidTestCase {

    @Mock
    Context context;
    private GeneralRealmManagerImpl realmManager;
    private static final int FAKE_USER_ID = 1;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        assertNotNull(context);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build());
        realmManager = new GeneralRealmManagerImpl(context);
    }

    @After
    public void tearDown() throws Exception {
        realmManager.getRealm().close();
    }

    public void testGet() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCover_url("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        verify(realmManager).getById(UserRealmModel.ID_COLUMN, FAKE_USER_ID, UserRealmModel.class);
        RxAssertions.assertThat((Observable<UserRealmModel>) realmManager
                .getById(UserRealmModel.ID_COLUMN, FAKE_USER_ID, UserRealmModel.class))
                .completes()
                .withoutErrors()
                .emissionsCount(1)
                .expectedValues(userRealmModel);
    }

    public void testGetAll() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        List<UserRealmModel> userRealmModels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            userRealmModel.setUserId(FAKE_USER_ID + i);
            userRealmModel.setCover_url("www.test.com");
            userRealmModel.setDescription("fake description");
            userRealmModel.setEmail("fake@email.com");
            userRealmModel.setFollowers(22);
            userRealmModel.setFullName("Fake Name");
            userRealmModels.add(userRealmModel);
            realmManager.put(userRealmModel);
        }
        verify(realmManager).getAll(UserRealmModel.class);
        RxAssertions.assertThat(realmManager.getAll(UserRealmModel.class))
                .completes()
                .withoutErrors()
                .emissionsCount(3)
                .expectedValues(userRealmModels);
    }

    public void testPut() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCover_url("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        verify(realmManager).put(userRealmModel);
        verify(realmManager).getById(UserRealmModel.ID_COLUMN, FAKE_USER_ID, UserRealmModel.class);
        RxAssertions.assertThat((Observable<UserRealmModel>) realmManager.put(userRealmModel))
                .completes()
                .withoutErrors()
                .emissionsCount(1)
                .expectedValues(userRealmModel);
        RxAssertions.assertThat((Observable<UserRealmModel>) realmManager
                .getById(UserRealmModel.ID_COLUMN, FAKE_USER_ID, UserRealmModel.class))
                .completes()
                .withoutErrors()
                .emissionsCount(1)
                .expectedValues(userRealmModel);
    }

    public void testPutAll() throws Exception {
        List<RealmObject> userRealmModels = new ArrayList<>();
        UserRealmModel userRealmModel;
        for (int i = 0; i < 10; i++) {
            userRealmModel = new UserRealmModel();
            userRealmModel.setUserId(FAKE_USER_ID + i);
            userRealmModel.setCover_url("www.test.com");
            userRealmModel.setDescription("fake description");
            userRealmModel.setEmail("fake@email.com");
            userRealmModel.setFollowers(22);
            userRealmModel.setFullName("Fake Name");
            userRealmModels.add(userRealmModel);
        }
        verify(realmManager).putAll(userRealmModels);
        for (int i = 0; i < 10; i++)
            verify(realmManager).getById(UserRealmModel.ID_COLUMN, FAKE_USER_ID + i, UserRealmModel.class);
    }

    public void testIsCached() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCover_url("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        assertTrue(realmManager.isCached(FAKE_USER_ID, UserRealmModel.ID_COLUMN, UserRealmModel.class));
    }

    public void testIsValid() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCover_url("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        assertTrue(realmManager.isItemValid(FAKE_USER_ID, UserRealmModel.ID_COLUMN, UserRealmModel.class));
    }

    public void testIsValid1() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCover_url("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        assertTrue(realmManager.areItemsValid(Constants.DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE));
    }

    public void testEvictAll() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        for (int i = 0; i < 3; i++) {
            userRealmModel.setUserId(FAKE_USER_ID + i);
            userRealmModel.setCover_url("www.test.com");
            userRealmModel.setDescription("fake description");
            userRealmModel.setEmail("fake@email.com");
            userRealmModel.setFollowers(22);
            userRealmModel.setFullName("Fake Name");
            realmManager.put(userRealmModel);
        }
        realmManager.evictAll(UserRealmModel.class);
        realmManager.getAll(UserRealmModel.class).subscribe(new Subscriber<Collection>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Collection userRealmModels) {
                assertEquals(userRealmModels.size(), 0);
            }
        });
    }

    public void testEvictById() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCover_url("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        realmManager.evictById(FAKE_USER_ID, UserRealmModel.class);
        realmManager.getById(UserRealmModel.ID_COLUMN, FAKE_USER_ID, UserRealmModel.class).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Object userRealmModel) {
                assertNull(userRealmModel);
            }
        });
    }

    public void testEvict() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCover_url("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        realmManager.evict(userRealmModel, UserRealmModel.class);
        realmManager.getById(UserRealmModel.ID_COLUMN, FAKE_USER_ID, UserRealmModel.class).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(Object userRealmModel) {
                assertNull(userRealmModel);
            }
        });
    }
}