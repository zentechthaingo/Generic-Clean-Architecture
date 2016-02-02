package com.zeyad.cleanarchetecturet.data.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zeyad.cleanarchetecturet.data.entity.UserRealmModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * {@link RealmManager} implementation.
 */
@Singleton
public class RealmManagerImpl implements RealmManager {

    private static final String SETTINGS_FILE_NAME = "com.zeyad.vjs.SETTINGS";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;
//    private static final String DEFAULT_FILE_NAME = "user_";
//    private final Context context;
//    private final File cacheDir;
//    private final UserJsonSerializer serializer;
//    private final FileManager fileManager;
//    private final ThreadExecutor threadExecutor;
//
//    /**
//     * Constructor of the class {@link RealmManagerImpl}.
//     *
//     * @param context             A
//     * @param userCacheSerializer {@link UserJsonSerializer} for object serialization.
//     * @param fileManager         {@link FileManager} for saving serialized objects to the file system.
//     */
//    @Inject
//    public RealmManagerImpl(Context context, UserJsonSerializer userCacheSerializer,
//                         FileManager fileManager, ThreadExecutor executor) {
//        if (context == null || userCacheSerializer == null || fileManager == null || executor == null) {
//            throw new IllegalArgumentException("Invalid null parameter");
//        }
//        this.context = context.getApplicationContext();
//        this.cacheDir = this.context.getCacheDir();
//        this.serializer = userCacheSerializer;
//        this.fileManager = fileManager;
//        this.threadExecutor = executor;
//    }
//
//    @Override
//    public Observable<UserEntity> get(final int userId) {
//        return Observable.createById(new Observable.OnSubscribe<UserEntity>() {
//            @Override
//            public void call(Subscriber<? super UserEntity> subscriber) {
//                File userEntityFile = RealmManagerImpl.this.buildFile(userId);
//                String fileContent = RealmManagerImpl.this.fileManager.readFileContent(userEntityFile);
//                UserEntity userEntity = RealmManagerImpl.this.serializer.deserialize(fileContent);
//                if (userEntity != null) {
//                    subscriber.onNext(userEntity);
//                    subscriber.onCompleted();
//                } else {
//                    subscriber.onError(new UserNotFoundException());
//                }
//            }
//        });
//    }
//
//    @Override
//    public void put(UserEntity userEntity) {
//        if (userEntity != null) {
//            File userEntitiyFile = this.buildFile(userEntity.getUserId());
//            if (!isCached(userEntity.getUserId())) {
//                String jsonString = this.serializer.serialize(userEntity);
//                this.executeAsynchronously(new CacheWriter(this.fileManager, userEntitiyFile,
//                        jsonString));
//                setLastCacheUpdateTimeMillis();
//            }
//        }
//    }
//
//    @Override
//    public boolean isCached(int userId) {
//        File userEntitiyFile = this.buildFile(userId);
//        return this.fileManager.exists(userEntitiyFile);
//    }
//
//    @Override
//    public boolean isValid() {
//        long currentTime = System.currentTimeMillis();
//        long lastUpdateTime = this.getLastCacheUpdateTimeMillis();
//
//        boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);
//
//        if (expired) {
//            this.evictAll();
//        }
//
//        return expired;
//    }
//
//    @Override
//    public void evictAll() {
//        this.executeAsynchronously(new CacheEvictor(this.fileManager, this.cacheDir));
//    }
//
//    /**
//     * Build a file, used to be inserted in the disk cache.
//     *
//     * @param userId The id user to build the file.
//     * @return A valid file.
//     */
//    private File buildFile(int userId) {
//        StringBuilder fileNameBuilder = new StringBuilder();
//        fileNameBuilder.append(this.cacheDir.getPath());
//        fileNameBuilder.append(File.separator);
//        fileNameBuilder.append(DEFAULT_FILE_NAME);
//        fileNameBuilder.append(userId);
//
//        return new File(fileNameBuilder.toString());
//    }
//
//    /**
//     * Set in millis, the last time the cache was accessed.
//     */
//    private void setLastCacheUpdateTimeMillis() {
//        long currentMillis = System.currentTimeMillis();
//        this.fileManager.writeToPreferences(this.context, SETTINGS_FILE_NAME,
//                SETTINGS_KEY_LAST_CACHE_UPDATE, currentMillis);
//    }
//
//    /**
//     * Get in millis, the last time the cache was accessed.
//     */
//    private long getLastCacheUpdateTimeMillis() {
//        return this.fileManager.getFromPreferences(this.context, SETTINGS_FILE_NAME,
//                SETTINGS_KEY_LAST_CACHE_UPDATE);
//    }
//
//    /**
//     * Executes a {@link Runnable} in another Thread.
//     *
//     * @param runnable {@link Runnable} to execute
//     */
//    private void executeAsynchronously(Runnable runnable) {
//        this.threadExecutor.execute(runnable);
//    }
//
//    /**
//     * {@link Runnable} class for writing to disk.
//     */
//    private static class CacheWriter implements Runnable {
//        private final FileManager fileManager;
//        private final File fileToWrite;
//        private final String fileContent;
//
//        CacheWriter(FileManager fileManager, File fileToWrite, String fileContent) {
//            this.fileManager = fileManager;
//            this.fileToWrite = fileToWrite;
//            this.fileContent = fileContent;
//        }
//
//        @Override
//        public void run() {
//            this.fileManager.writeToFile(fileToWrite, fileContent);
//        }
//    }
//
//    /**
//     * {@link Runnable} class for evicting all the cached files
//     */
//    private static class CacheEvictor implements Runnable {
//        private final FileManager fileManager;
//        private final File cacheDir;
//
//        CacheEvictor(FileManager fileManager, File cacheDir) {
//            this.fileManager = fileManager;
//            this.cacheDir = cacheDir;
//        }
//
//        @Override
//        public void run() {
//            this.fileManager.clearDirectory(this.cacheDir);
//        }
//    }

    //-------------------------------------------------------------------------------------------//
    @Inject
    public RealmManagerImpl(Context context) {
        mRealm = Realm.getDefaultInstance();
        this.context = context;
    }

    private Realm mRealm;
    private Context context;

    @Override
    public Observable<UserRealmModel> get(final int userId) {
        mRealm = Realm.getDefaultInstance();
        return mRealm.where(UserRealmModel.class).equalTo("id", userId).findFirstAsync().asObservable();
    }

    @Override
    public Observable<RealmResults<UserRealmModel>> getAll() {
        mRealm = Realm.getDefaultInstance();
        return mRealm.where(UserRealmModel.class).findAllAsync().asObservable();
    }

    @Override
    public void put(final UserRealmModel userEntity) {
        if (userEntity != null) {
            if (!isCached(userEntity.getUserId())) {
                Observable.create(new Observable.OnSubscribe<UserRealmModel>() {
                    @Override
                    public void call(final Subscriber<? super UserRealmModel> subscriber) {
                        mRealm = Realm.getDefaultInstance();
                        mRealm.beginTransaction();
                        mRealm.copyToRealmOrUpdate(userEntity);
                        mRealm.commitTransaction();
                        writeToPreferences(System.currentTimeMillis());
                        subscriber.onNext(userEntity);
                        subscriber.onCompleted();
                    }
                }).subscribe(new Observer<UserRealmModel>() {
                    @Override
                    public void onCompleted() {
                        Log.d("RealmManagerImpl", "UserRealmModel insert complete");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("RealmManagerImpl", "Failed to insert UserRealmModel", e);
                    }

                    @Override
                    public void onNext(UserRealmModel userRealmModel) {
                        Log.d("RealmManagerImpl", "UserRealmModel insert successful, name: "
                                + userRealmModel.getFullName());
                    }
                });
            }
        }
    }

    @Override
    public boolean isCached(int userId) {
        mRealm = Realm.getDefaultInstance();
        mRealm.beginTransaction();
        boolean isCached = mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirst() != null;
        mRealm.commitTransaction();
        return isCached;
    }

    @Override
    public boolean isValid(int userId) {
        return isCached(userId) && isValid();
    }

    @Override
    public boolean isValid() {
        if (((System.currentTimeMillis() - getFromPreferences()) > EXPIRATION_TIME)) {
            evictAll();
            return false;
        } else
            return true;
    }

    @Override
    public void evictAll() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                mRealm.where(UserRealmModel.class).findAll().clear();
                mRealm.commitTransaction();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
            }

            @Override
            public void onNext(Boolean file) {
                Log.d("RealmManagerImpl", "UserRealmModel evict successful");
            }
        });
    }

    @Override
    public void evictById(final int userId) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                mRealm.where(UserRealmModel.class).equalTo("userId", userId).findFirst().removeFromRealm();
                mRealm.commitTransaction();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
            }

            @Override
            public void onNext(Boolean file) {
                Log.d("RealmManagerImpl", "UserRealmModel evict successful");
            }
        });
    }

    @Override
    public void evict(final UserRealmModel userRealmModel) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                mRealm = Realm.getDefaultInstance();
                mRealm.beginTransaction();
                userRealmModel.removeFromRealm();
                mRealm.commitTransaction();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        }).subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                Log.d("RealmManagerImpl", "UserRealmModel evict complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("RealmManagerImpl", "Failed to evict UserRealmModel", e);
            }

            @Override
            public void onNext(Boolean file) {
                Log.d("RealmManagerImpl", "UserRealmModel evict successful");
            }
        });
    }

    /**
     * Write a value to a user preferences file.
     *
     * @param value A long representing the value to be inserted.
     */
    private void writeToPreferences(long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS_FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putLong(SETTINGS_KEY_LAST_CACHE_UPDATE, value);
        editor.apply();
    }

    /**
     * Get a value from a user preferences file.
     *
     * @return A long representing the value retrieved from the preferences file.
     */
    private long getFromPreferences() {
        return context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE)
                .getLong(SETTINGS_KEY_LAST_CACHE_UPDATE, 0);
    }
}