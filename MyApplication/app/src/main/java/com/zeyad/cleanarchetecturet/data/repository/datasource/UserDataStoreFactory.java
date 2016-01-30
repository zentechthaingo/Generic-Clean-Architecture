package com.zeyad.cleanarchetecturet.data.repository.datasource;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zeyad.cleanarchetecturet.data.cache.UserCache;
import com.zeyad.cleanarchetecturet.data.exception.NetworkConnectionException;
import com.zeyad.cleanarchetecturet.data.net.RestApiImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory that creates different implementations of {@link UserDataStore}.
 */
@Singleton
public class UserDataStoreFactory {

    private final Context context;
    private final UserCache userCache;

    @Inject
    public UserDataStoreFactory(Context context, UserCache userCache) {
        if (context == null || userCache == null)
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        this.context = context.getApplicationContext();
        this.userCache = userCache;
    }

//    /**
//     * Create {@link UserDataStore} from a user id.
//     */
//    public UserDataStore create(int userId) throws NetworkConnectionException {
//        UserDataStore userDataStore;
//        if (!this.userCache.isExpired(userId) && this.userCache.isCached(userId))
//            userDataStore = new DiskUserDataStore(this.userCache);
//        else if (isThereInternetConnection())
//            userDataStore = createCloudDataStore();
//        else throw new NetworkConnectionException();
//        return userDataStore;
//    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore create(int userId) throws NetworkConnectionException {
        UserDataStore userDataStore;
        if (!this.userCache.isExpired() && this.userCache.isCached(userId))
            userDataStore = new DiskUserDataStore(this.userCache);
        else
            userDataStore = createCloudDataStore();
        return userDataStore;
    }

    // TODO: 1/30/16 remove!

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud.
     */
    public UserDataStore createCloudDataStore() {
        return new CloudUserDataStore(new RestApiImpl(), userCache);
    }

//    /**
//     * Create {@link UserDataStore} from a user id.
//     */
//    public UserDataStore create(int userId) {
//        UserDataStore userDataStore;
//        if (realmManager.isValid(userId))
//            userDataStore = new DiskUserDataStore(realmManager);
//        else
//            userDataStore = createCloudDataStore();
//        return userDataStore;
//    }
//
//    /**
//     * Create {@link UserDataStore} to retrieve data from the Cloud.
//     */
//    public UserDataStore createCloudDataStore() {
//        return new CloudUserDataStore(new RestApiImpl(), realmManager);
//    }

    /**
     * Checks if the device has any active internet connection.
     *
     * @return true device with internet connection, otherwise false.
     */
    private boolean isThereInternetConnection() {
        boolean isConnected;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }
}