package com.grability.rappitendero.domain.interactors;

import android.support.annotation.NonNull;

import java.io.File;

import rx.Subscriber;

/**
 * @author zeyad on 7/29/16.
 */
public class UploadRequest {

    private File mFile;
    private String mUrl;
    private Subscriber mSubscriber;
    private Class mDataClass, mPresentationClass, mDomainClass;
    private boolean mPersist;

    public UploadRequest(@NonNull UploadRequestBuilder uploadRequestBuilder) {
        mUrl = uploadRequestBuilder.getUrl();
        mDataClass = uploadRequestBuilder.getDataClass();
        mPresentationClass = uploadRequestBuilder.getPresentationClass();
        mDomainClass = uploadRequestBuilder.getDomainClass();
        mPersist = uploadRequestBuilder.isPersist();
        mSubscriber = uploadRequestBuilder.getSubscriber();
        mFile = uploadRequestBuilder.getFile();
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

    public File getFile() {
        return mFile;
    }

    public static class UploadRequestBuilder {

        private File mFile;
        private String mUrl;
        private Subscriber mSubscriber;
        private Class mDataClass, mPresentationClass, mDomainClass;
        private boolean mPersist;

        public UploadRequestBuilder(Class dataClass, boolean persist) {
            mDataClass = dataClass;
            mPersist = persist;
        }

        @NonNull
        public UploadRequestBuilder url(String url) {
            mUrl = url;
            return this;
        }

        @NonNull
        public UploadRequestBuilder presentationClass(Class presentationClass) {
            mPresentationClass = presentationClass;
            return this;
        }

        @NonNull
        public UploadRequestBuilder domainClass(Class domainClass) {
            mDomainClass = domainClass;
            return this;
        }

        @NonNull
        public UploadRequestBuilder subscriber(Subscriber subscriber) {
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
        public UploadRequest build() {
            return new UploadRequest(this);
        }

        public File getFile() {
            return mFile;
        }

        @NonNull
        public UploadRequestBuilder file(File file) {
            mFile = file;
            return this;
        }
    }
}