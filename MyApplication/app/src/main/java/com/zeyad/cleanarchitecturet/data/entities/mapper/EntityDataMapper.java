package com.zeyad.cleanarchitecturet.data.entities.mapper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecturet.domain.models.User;
import com.zeyad.cleanarchitecturet.presentation.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmObject;

@Singleton
public class EntityDataMapper {
    private Gson gson;

    @Inject
    public EntityDataMapper() {
        gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();
    }

    /**
     * Transform a {@link UserEntity} into an {@link User}.
     *
     * @param userRealmModel Object to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public UserModel transformToDomain(Object userRealmModel, Class clazz) {
        if (userRealmModel != null) {
            UserEntity cast = gson.fromJson(gson.toJson(userRealmModel), UserEntity.class);
            UserModel user = new UserModel(cast.getUserId());
            user.setCoverUrl(cast.getCoverUrl());
            user.setFullName(cast.getFullName());
            user.setDescription(cast.getDescription());
            user.setFollowers(cast.getFollowers());
            user.setEmail(cast.getEmail());
            return user;
        }
        return null;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link UserEntity} if valid {@link UserRealmModel} otherwise null.
     */
    public Collection<UserModel> transformAllToDomain(Collection userRealmModels, Class clazz) {
        Collection<UserModel> userEntityList = new ArrayList<>();
        for (int i = 0; i < userRealmModels.size(); i++)
            userEntityList.add(transformToDomain(userRealmModels.toArray()[i], clazz));
//        userEntityList.add(gson.fromJson(gson.toJson(userRealmModels.iterator().next()),
//                clazz));
        return userEntityList;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param collection Objects to be transformed.
     * @return {@link List <UserRealmModel>} if valid {@link UserRealmModel} otherwise null.
     */
    public Collection<UserRealmModel> transformAllToRealm(Collection collection) {
        List<UserRealmModel> userRealmModels = new ArrayList<>();
        UserRealmModel userRealmModel;
        for (Object userEntity : collection) {
            userRealmModel = transformToRealm(userEntity);
            if (userRealmModel != null)
                userRealmModels.add(userRealmModel);
        }
        return userRealmModels;
    }

    /**
     * Transform a {@link UserEntity} into an {@link User}.
     *
     * @param item Object to be transformed.
     * @return {@link UserRealmModel} if valid {@link UserEntity} otherwise null.
     */
    public UserRealmModel transformToRealm(Object item) {
        if (item != null)
            try {
                return gson.fromJson(String.valueOf(new JSONObject(gson.toJson(item))),
                        UserRealmModel.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }
}