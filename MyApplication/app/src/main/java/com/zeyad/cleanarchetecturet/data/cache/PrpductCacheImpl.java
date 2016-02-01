package com.zeyad.cleanarchetecturet.data.cache;

import android.content.Context;

import com.zeyad.cleanarchetecturet.data.cache.serializer.ProductJsonSerializer;
import com.zeyad.cleanarchetecturet.data.cache.serializer.UserJsonSerializer;
import com.zeyad.cleanarchetecturet.data.entity.ProductEntity;
import com.zeyad.cleanarchetecturet.data.exception.UserNotFoundException;
import com.zeyad.cleanarchetecturet.domain.executor.ThreadExecutor;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
// TODO: 1/29/16 Change Name!

/**
 * {@link UserCache} implementation.
 */
@Singleton
public class PrpductCacheImpl implements ProductCache {

    private static final String SETTINGS_FILE_NAME = "com.fernandocejas.android10.SETTINGS";
    private static final String SETTINGS_KEY_LAST_CACHE_UPDATE = "last_cache_update";

    private static final String DEFAULT_FILE_NAME = "user_";
    private static final long EXPIRATION_TIME = 60 * 10 * 1000;

    private final Context context;
    private final File cacheDir;
    private final ProductJsonSerializer serializer;
    private final UserFileManager userFileManager;
    private final ThreadExecutor threadExecutor;

    /**
     * Constructor of the class {@link PrpductCacheImpl}.
     *
     * @param context               A
     * @param productJsonSerializer {@link UserJsonSerializer} for object serialization.
     * @param userFileManager       {@link UserFileManager} for saving serialized objects to the file system.
     */
    @Inject
    public PrpductCacheImpl(Context context, ProductJsonSerializer productJsonSerializer,
                            UserFileManager userFileManager, ThreadExecutor executor) {
        if (context == null || productJsonSerializer == null || userFileManager == null || executor == null) {
            throw new IllegalArgumentException("Invalid null parameter");
        }
        this.context = context.getApplicationContext();
        this.cacheDir = this.context.getCacheDir();
        this.serializer = productJsonSerializer;
        this.userFileManager = userFileManager;
        this.threadExecutor = executor;
    }

    @Override
    public Observable<ProductEntity> get(final int userId) {
        return Observable.create(new Observable.OnSubscribe<ProductEntity>() {
            @Override
            public void call(Subscriber<? super ProductEntity> subscriber) {
                File productEntityFile = buildFile(userId);
                String fileContent = userFileManager.readFileContent(productEntityFile);
                ProductEntity productEntity = serializer.deserialize(fileContent);
                if (productEntity != null) {
                    subscriber.onNext(productEntity);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new UserNotFoundException());
                }
            }
        });
    }

    @Override
    public void put(ProductEntity productEntity) {
        if (productEntity != null) {
            File productEntityFile = buildFile(Integer.parseInt(productEntity.getProduct_id()));
            if (!isCached(Integer.parseInt(productEntity.getProduct_id()))) {
                String jsonString = serializer.serialize(productEntity);
                this.executeAsynchronously(new CacheWriter(this.userFileManager, productEntityFile,
                        jsonString));
                setLastCacheUpdateTimeMillis();
            }
        }
    }

    @Override
    public boolean isCached(int productId) {
        File productEntitiyFile = buildFile(productId);
        return userFileManager.exists(productEntitiyFile);
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
        executeAsynchronously(new CacheEvictor(this.userFileManager, this.cacheDir));
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
        this.userFileManager.writeToPreferences(this.context, SETTINGS_FILE_NAME,
                SETTINGS_KEY_LAST_CACHE_UPDATE, currentMillis);
    }

    /**
     * Get in millis, the last time the cache was accessed.
     */
    private long getLastCacheUpdateTimeMillis() {
        return this.userFileManager.getFromPreferences(this.context, SETTINGS_FILE_NAME,
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
        private final UserFileManager userFileManager;
        private final File fileToWrite;
        private final String fileContent;

        CacheWriter(UserFileManager userFileManager, File fileToWrite, String fileContent) {
            this.userFileManager = userFileManager;
            this.fileToWrite = fileToWrite;
            this.fileContent = fileContent;
        }

        @Override
        public void run() {
            userFileManager.writeToFile(fileToWrite, fileContent);
        }
    }

    /**
     * {@link Runnable} class for evicting all the cached files
     */
    private static class CacheEvictor implements Runnable {
        private final UserFileManager userFileManager;
        private final File cacheDir;

        CacheEvictor(UserFileManager userFileManager, File cacheDir) {
            this.userFileManager = userFileManager;
            this.cacheDir = cacheDir;
        }

        @Override
        public void run() {
            this.userFileManager.clearDirectory(this.cacheDir);
        }
    }

    //-------------------------------------------------------------------------------------------//

    //    @Inject
//    public UserCacheImpl(Context context) {
//        mRealm = Realm.getInstance(context);
//    }
//
    private Realm mRealm;

    //
//    @Override
//    public Observable<ProductEntity> get(final int userId) {
//        return Observable.create(new Observable.OnSubscribe<ProductEntity>() {
//            @Override
//            public void call(Subscriber<? super ProductEntity> subscriber) {
//                ProductEntity productEntity = mRealm.where(ProductEntity.class).equalTo("id", userId).findAll().first();
//                if (productEntity != null) {
//                    subscriber.onNext(productEntity);
//                    subscriber.onCompleted();
//                } else {
//                    subscriber.onError(new UserNotFoundException());
//                }
//            }
//        });
//    }
//
//    @Override
//    public void put(final ProductEntity productEntity) {
//        if (productEntity != null) {
//            if (!isCached(productEntity.getUserId())) {
//                Observable.create(new Observable.OnSubscribe<ProductEntity>() {
//                    @Override
//                    public void call(final Subscriber<? super ProductEntity> subscriber) {
//                        ProductEntity roomEntity2 = mRealm.createObject(ProductEntity.class);
//                        productEntity.setLastUpdateTimeMillis(System.currentTimeMillis());
//                        roomEntity2 = productEntity;
//                        subscriber.onNext(roomEntity2);
//                        subscriber.onCompleted();
//                    }
//                }).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(new Observer<ProductEntity>() {
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
//                    public void onNext(ProductEntity file) {// Called each time the observable emits data
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
//        return mRealm.where(ProductEntity.class).equalTo("id", userId).findAll().first().isValid();
//    }
//
//    @Override
//    public boolean isExpired(int userId) {
//        ProductEntity productEntity = mRealm.where(ProductEntity.class).equalTo("id", userId).findAll().first();
////        if (expired)
////            evictAll();
//        return ((System.currentTimeMillis() - productEntity.getLastUpdateTimeMillis()) > EXPIRATION_TIME);
//    }
//
//    @Override
//    public void evictAll() {
//        mRealm.where(ProductEntity.class).findAll().clear();
//    }
//
    @Override
    public Observable<Boolean> evictById(final int userId) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
//                mRealm.where(ProductEntity.class).equalTo("id", userId).findAll().first().removeFromRealm();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> evict(final ProductEntity productEntity) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
//                productEntity.removeFromRealm();
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }
}