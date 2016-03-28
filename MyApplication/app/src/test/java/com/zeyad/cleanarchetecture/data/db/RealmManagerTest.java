package com.zeyad.cleanarchetecture.data.db;

import android.content.Context;
import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.data.db.RealmManagerImpl;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Subscriber;

import static org.mockito.Mockito.verify;

// FIXME: 3/10/16 Update with current RealmManager!
public class RealmManagerTest extends AndroidTestCase {

    private RealmManagerImpl realmManager;
    @Mock
    Context context;
    private static final int FAKE_USER_ID = 1;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        assertNotNull(context);
//        RealmConfiguration config = ;
//        Realm.deleteRealm(config);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build());
        realmManager = new RealmManagerImpl(context);
    }

    @After
    public void tearDown() throws Exception {
//        realmManager.evictAll();
        realmManager.getRealm().close();
    }

    @Test
    public void testGet() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCoverUrl("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        verify(realmManager).get(FAKE_USER_ID);
    }

    @Test
    public void testGetAll() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        for (int i = 0; i < 3; i++) {
            userRealmModel.setUserId(FAKE_USER_ID + i);
            userRealmModel.setCoverUrl("www.test.com");
            userRealmModel.setDescription("fake description");
            userRealmModel.setEmail("fake@email.com");
            userRealmModel.setFollowers(22);
            userRealmModel.setFullName("Fake Name");
            realmManager.put(userRealmModel);
        }
        verify(realmManager).getAll();
    }

    @Test
    public void testPut() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCoverUrl("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        verify(realmManager).put(userRealmModel);
        verify(realmManager).get(FAKE_USER_ID);
    }

    @Test
    public void testIsCached() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCoverUrl("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        assertTrue(realmManager.isCached(FAKE_USER_ID));
    }

    @Test
    public void testIsValid() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCoverUrl("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        assertTrue(realmManager.isCached(FAKE_USER_ID));
    }

    @Test
    public void testIsValid1() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCoverUrl("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        assertTrue(realmManager.areUsersValid());
    }

    // TODO: 2/3/16 finish!
    @Test
    public void testEvictAll() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        for (int i = 0; i < 3; i++) {
            userRealmModel.setUserId(FAKE_USER_ID + i);
            userRealmModel.setCoverUrl("www.test.com");
            userRealmModel.setDescription("fake description");
            userRealmModel.setEmail("fake@email.com");
            userRealmModel.setFollowers(22);
            userRealmModel.setFullName("Fake Name");
            realmManager.put(userRealmModel);
        }
        realmManager.evictAll();
        realmManager.getAll()
                .asObservable()
                .subscribe(new Subscriber<Collection<UserRealmModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        fail();
                    }

                    @Override
                    public void onNext(Collection<UserRealmModel> userRealmModels) {
                        assertEquals(userRealmModels.size(), 0);
                    }
                });
    }

    // TODO: 2/3/16 finish!
    @Test
    public void testEvictById() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCoverUrl("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        realmManager.evictById(FAKE_USER_ID);
        realmManager.get(FAKE_USER_ID).asObservable().subscribe(new Subscriber<UserRealmModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(UserRealmModel userRealmModel) {
                assertNull(userRealmModel);
            }
        });
    }

    // TODO: 2/3/16 finish!
    @Test
    public void testEvict() throws Exception {
        UserRealmModel userRealmModel = new UserRealmModel();
        userRealmModel.setUserId(FAKE_USER_ID);
        userRealmModel.setCoverUrl("www.test.com");
        userRealmModel.setDescription("fake description");
        userRealmModel.setEmail("fake@email.com");
        userRealmModel.setFollowers(22);
        userRealmModel.setFullName("Fake Name");
        realmManager.put(userRealmModel);
        realmManager.evict(userRealmModel);
        realmManager.get(FAKE_USER_ID).asObservable().subscribe(new Subscriber<UserRealmModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onNext(UserRealmModel userRealmModel) {
                assertNull(userRealmModel);
            }
        });
    }
}