package com.zeyad.cleanarchitecture.data.entities;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class UserRealmModel extends RealmObject {

    public final static String FULL_NAME_COLUMN = "full_name", ID_COLUMN = "userId", COVER_URL = "coverUrl",
            EMAIL = "email", DESCRIPTION = "description", FOLLOWERS = "followers";

    @PrimaryKey
    @SerializedName("id")
    private int userId;

    @SerializedName("cover_url")
    private String cover_url;

    @SerializedName(FULL_NAME_COLUMN)
    private String full_name;

    @SerializedName("description")
    private String description;

    @SerializedName("followers")
    private int followers;

    @SerializedName("email")
    private String email;

    public UserRealmModel() {

    }

    public int getUserId() {
        return userId;
    }

    public UserRealmModel setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String fullName) {
        this.full_name = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}