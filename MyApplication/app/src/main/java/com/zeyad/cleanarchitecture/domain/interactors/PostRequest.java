package com.zeyad.cleanarchitecture.domain.interactors;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import rx.Subscriber;

/**
 * @author zeyad on 7/29/16.
 */
public class PostRequest {

    private String mUrl;
    private Subscriber mSubscriber;
    private Class mDataClass, mPresentationClass, mDomainClass;
    private boolean mPersist;
    private JSONObject mJsonObject;
    private JSONArray mJsonArray;
    private HashMap<String, Object> mKeyValuePairs;

    public PostRequest(@NonNull PostRequestBuilder postRequestBuilder) {
        mUrl = postRequestBuilder.getUrl();
        mDataClass = postRequestBuilder.getDataClass();
        mPresentationClass = postRequestBuilder.getPresentationClass();
        mDomainClass = postRequestBuilder.getDomainClass();
        mPersist = postRequestBuilder.isPersist();
        mSubscriber = postRequestBuilder.getSubscriber();
        mKeyValuePairs = postRequestBuilder.getKeyValuePairs();
        mJsonObject = postRequestBuilder.getJsonObject();
        mJsonArray = postRequestBuilder.getJsonArray();
    }

    public String getUrl() {
        return mUrl;
    }

    public Subscriber getSubscriber() {
        return mSubscriber;
    }

    public Class getDataClass() {
        return mDataClass;
    }

    public Class getPresentationClass() {
        return mPresentationClass;
    }

    public Class getDomainClass() {
        return mDomainClass;
    }

    public boolean isPersist() {
        return mPersist;
    }

    public JSONObject getJsonObject() {
        return mJsonObject;
    }

    public JSONArray getJsonArray() {
        return mJsonArray;
    }

    public HashMap<String, Object> getKeyValuePairs() {
        return mKeyValuePairs;
    }

    public static class PostRequestBuilder {

        private JSONArray mJsonArray;
        private JSONObject mJsonObject;
        private HashMap<String, Object> mKeyValuePairs;
        private String mUrl;
        private Subscriber mSubscriber;
        private Class mDataClass, mPresentationClass, mDomainClass;
        private boolean mPersist;

        public PostRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public PostRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public PostRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public PostRequestBuilder domainClass(Class domainClass) {
            mDomainClass = domainClass;
            return this;
        }

        @NonNull
        public PostRequestBuilder subscriber(Subscriber subscriber) {
            mSubscriber = subscriber;
            return this;
        }

        public String getUrl() {
            return mUrl;
        }

        public Subscriber getSubscriber() {
            return mSubscriber;
        }

        public Class getDataClass() {
            return mDataClass;
        }

        public Class getPresentationClass() {
            return mPresentationClass;
        }

        public Class getDomainClass() {
            return mDomainClass;
        }

        public boolean isPersist() {
            return mPersist;
        }

        @NonNull
        public PostRequestBuilder jsonObject(JSONObject jsonObject) {
            mJsonObject = jsonObject;
            return this;
        }

        @NonNull
        public PostRequestBuilder jsonArray(JSONArray jsonArray) {
            mJsonArray = jsonArray;
            return this;
        }

        @NonNull
        public PostRequestBuilder hashMap(HashMap<String, Object> bundle) {
            mKeyValuePairs = bundle;
            return this;
        }

        @NonNull
        public PostRequest build() {
            return new PostRequest(this);
        }

        public JSONObject getJsonObject() {
            return mJsonObject;
        }

        public JSONArray getJsonArray() {
            return mJsonArray;
        }

        public HashMap<String, Object> getKeyValuePairs() {
            return mKeyValuePairs;
        }
    }
}