package com.zeyad.cleanarchitecture.data.entities;

import com.google.gson.annotations.SerializedName;

/**
 * User Entity used in the data layer.
 */
public class UserEntity {
    @SerializedName("id")
    private int userId;
    @SerializedName("cover_url")
    private String coverUrl;
    @SerializedName("full_name")
    private String full_name;
    @SerializedName("description")
    private String description;
    @SerializedName("followers")
    private int followers;
    @SerializedName("email")
    private String email;
    private long lastUpdateTimeMillis;

    public UserEntity() {
        //empty
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public void setLastUpdateTimeMillis(long lastUpdateTimeMillis) {
        this.lastUpdateTimeMillis = lastUpdateTimeMillis;
    }

    public long getLastUpdateTimeMillis() {
        return lastUpdateTimeMillis;
    }
}