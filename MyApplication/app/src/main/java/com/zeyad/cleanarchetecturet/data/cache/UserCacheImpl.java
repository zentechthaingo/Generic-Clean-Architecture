package com.zeyad.cleanarchetecturet.data.cache;

import android.content.Context;

import com.zeyad.cleanarchetecturet.data.cache.serializer.JsonSerializer;
import com.zeyad.cleanarchetecturet.data.entity.UserEntity;
import com.zeyad.cleanarchetecturet.data.exception.UserNotFoundException;
import com.zeyad.cleanarchetecturet.domain.executor.ThreadExecutor;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
// TODO: 1/29/16 Change Name!

/**
 * {@link UserCache} implementation.
 */
@Singleton
public class UserCacheImpl implements UserCache {

    private static final String SETTINGS_FILE_NAME = "com.fernandocejas.android10.SETTINGS";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";

    private static final String DEFAULT_FILE_NAME = "user_";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    private final Context context;
    private final File cacheDir;
    private final JsonSerializer serializer;
    private final FileManager fileManager;
    private final ThreadExecutor threadExecutor;

    /**
     * Constructor of the class {@link UserCacheImpl}.
     *
     * @param context             A
     * @param userCacheSerializer {@link JsonSerializer} for object serialization.
     * @param fileManager         {@link FileManager} for saving serialized objects to the file system.
     */
    @Inject
    public UserCacheImpl(Context context, JsonSerializer userCacheSerializer,
                         FileManager fileManager, ThreadExecutor executor) {
        if (context == null || userCacheSerializer == null || fileManager == null || executor == null) {
            throw new IllegalArgumentException("Invalid null parameter");
        }
        this.context = context.getApplicationContext();
        this.cacheDir = this.context.getCacheDir();
        this.serializer = userCacheSerializer;
        this.fileManager = fileManager;
        this.threadExecutor = executor;
    }

    @Override
    public Observable<UserEntity> get(final int userId) {
        return Observable.create(new Observable.OnSubscribe<UserEntity>() {
            @Override
            public void call(Subscriber<? super UserEntity> subscriber) {
                File userEntityFile = UserCacheImpl.this.buildFile(userId);
                String fileContent = UserCacheImpl.this.fileManager.readFileContent(userEntityFile);
                UserEntity userEntity = UserCacheImpl.this.serializer.deserialize(fileContent);
                if (userEntity != null) {
                    subscriber.onNext(userEntity);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public void put(UserEntity userEntity) {
        if (userEntity != null) {
            File userEntityFile = this.buildFile(userEntity.getUserId());
            if (!isCached(userEntity.getUserId())) {
                String jsonString = this.serializer.serialize(userEntity);
                this.executeAsynchronously(new CacheWriter(this.fileManager, userEntityFile,
                        jsonString));
                setLastCacheUpdateTimeMillis();
            }
        }
    }

    @Override
    public boolean isCached(int userId) {
        File userEntitiyFile = this.buildFile(userId);
        return this.fileManager.exists(userEntitiyFile);
    }

    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long lastUpdateTime = this.getLastCacheUpdateTimeMillis();
        boolean expired = ((currentTime - lastUpdateTime) > EXPIRATION_TIME);
        if (expired)
            evictAll();
        return expired;
    }

    @Override
    public void evictAll() {
        executeAsynchronously(new CacheEvictor(this.fileManager, this.cacheDir));
    }

    /**
     * Build a file, used to be inserted in the disk cache.
     *
     * @param userId The id user to build the file.
     * @return A valid file.
     */
    private File buildFile(int userId) {
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(this.cacheDir.getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(DEFAULT_FILE_NAME);
        fileNameBuilder.append(userId);
        return new File(fileNameBuilder.toString());
    }

    /**
     * Set in millis, the last time the cache was accessed.
     */
    private void setLastCacheUpdateTimeMillis() {
        long currentMillis = System.currentTimeMillis();
        this.fileManager.writeToPreferences(this.context, SETTINGS_FILE_NAME,
                SETTINGS_KEY_LAST_CACHE_UPDATE, currentMillis);
    }

    /**
     * Get in millis, the last time the cache was accessed.
     */
    private long getLastCacheUpdateTimeMillis() {
        return this.fileManager.getFromPreferences(this.context, SETTINGS_FILE_NAME,
                SETTINGS_KEY_LAST_CACHE_UPDATE);
    }

    /**
     * Executes a {@link Runnable} in another Thread.
     *
     * @param runnable {@link Runnable} to execute
     */
    private void executeAsynchronously(Runnable runnable) {
        threadExecutor.execute(runnable);
    }

    /**
     * {@link Runnable} class for writing to disk.
     */
    private static class CacheWriter implements Runnable {
        private final FileManager fileManager;
        private final File fileToWrite;
        private final String fileContent;

        CacheWriter(FileManager fileManager, File fileToWrite, String fileContent) {
            this.fileManager = fileManager;
            this.fileToWrite = fileToWrite;
            this.fileContent = fileContent;
        }

        @Override
        public void run() {
            fileManager.writeToFile(fileToWrite, fileContent);
        }
    }

    /**
     * {@link Runnable} class for evicting all the cached files
     */
    private static class CacheEvictor implements Runnable {
        private final FileManager fileManager;
        private final File cacheDir;

        CacheEvictor(FileManager fileManager, File cacheDir) {
            this.fileManager = fileManager;
            this.cacheDir = cacheDir;
        }

        @Override
        public void run() {
            this.fileManager.clearDirectory(this.cacheDir);
        }
    }

    //-------------------------------------------------------------------------------------------//

//    @Inject
//    public UserCacheImpl(Context context) {
//        mRealm = Realm.getInstance(context);
//    }
//
//    private Realm mRealm;
//
//    @Override
//    public Observable<UserEntity> get(final int userId) {
//        return Observable.create(new Observable.OnSubscribe<UserEntity>() {
//            @Override
//            public void call(Subscriber<? super UserEntity> subscriber) {
//                UserEntity userEntity = mRealm.where(UserEntity.class).equalTo("id", userId).findAll().first();
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
//    public void put(final UserEntity userEntity) {
//        if (userEntity != null) {
//            if (!isCached(userEntity.getUserId())) {
//                Observable.create(new Observable.OnSubscribe<UserEntity>() {
//                    @Override
//                    public void call(final Subscriber<? super UserEntity> subscriber) {
//                        UserEntity roomEntity2 = mRealm.createObject(UserEntity.class);
//                        userEntity.setLastUpdateTimeMillis(System.currentTimeMillis());
//                        roomEntity2 = userEntity;
//                        subscriber.onNext(roomEntity2);
//                        subscriber.onCompleted();
//                    }
//                }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(new Observer<UserEntity>() {
//                    @Override
//                    public void onCompleted() { // Called when the observable has no more data to emit
////                        Log.d("Rx image caching", "Image download complete");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {// Called when the observable encounters an error
////                        Log.d("Rx image caching", "Failed to download image", e);
////                        subscriber.onError(e);
//                    }
//
//                    @Override
//                    public void onNext(UserEntity file) {// Called each time the observable emits data
////                        Log.d("Rx image caching", "Image download successful, name: " + file.getName());
////                        subscriber.onNext(file);
//                    }
//                });
//            }
//        }
//    }
//
//    @Override
//    public boolean isCached(int userId) {
//        return mRealm.where(UserEntity.class).equalTo("id", userId).findAll().first().isValid();
//    }
//
//    @Override
//    public boolean isExpired(int userId) {
//        UserEntity userEntity = mRealm.where(UserEntity.class).equalTo("id", userId).findAll().first();
////        if (expired)
////            evictAll();
//        return ((System.currentTimeMillis() - userEntity.getLastUpdateTimeMillis()) > EXPIRATION_TIME);
//    }
//
//    @Override
//    public void evictAll() {
//        mRealm.where(UserEntity.class).findAll().clear();
//    }
//
//    @Override
//    public Observable<Boolean> evictById(final int userId) {
//        return Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                mRealm.where(UserEntity.class).equalTo("id", userId).findAll().first().removeFromRealm();
//                subscriber.onNext(true);
//                subscriber.onCompleted();
//            }
//        });
//    }
//
//    @Override
//    public Observable<Boolean> evict(final UserEntity userEntity) {
//        return Observable.create(new Observable.OnSubscribe<Boolean>() {
//            @Override
//            public void call(Subscriber<? super Boolean> subscriber) {
//                userEntity.removeFromRealm();
//                subscriber.onNext(true);
//                subscriber.onCompleted();
//            }
//        });
//    }
}