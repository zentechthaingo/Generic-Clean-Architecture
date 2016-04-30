package com.zeyad.cleanarchitecture.utilities;

public class Constants {

    public static final String BASE_URL = "https://s3-eu-west-1.amazonaws.com/developer-application-test",
            IMAGE_EXTENSION = ".jpg", BASE_IMAGE_NAME_CACHED = "image_";

    // Firebase credentials
    public static final String FIREBASE_URL = "https://shoplistplusplus-jps37.firebaseio.com/";

    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";

    public static String CACHE_DIR;

    public static final String SETTINGS_FILE_NAME = "com.zeyad.cleanarchitecture.SETTINGS",
            COLLECTION_SETTINGS_KEY_LAST_CACHE_UPDATE = "collection_last_cache_update",
            DETAIL_SETTINGS_KEY_LAST_CACHE_UPDATE = "detail_last_cache_update",
            API_BASE_URL = "http://www.android10.org/myapi/";

    public static final long EXPIRATION_TIME = 600000;
}