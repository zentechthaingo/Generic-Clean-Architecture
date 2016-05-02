package com.zeyad.cleanarchitecture.data.entities.mapper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmObject;

// TODO: 3/24/16 Generalize!
@Singleton
public class EntityDataMapper {
    protected Gson gson;

    @Inject
    public EntityDataMapper() {
        gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
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
     * Transform a {@link User} into an {@link User}.
     *
     * @param userDomain Object to be transformed.
     * @return {@link User} if valid {@link User} otherwise null.
     */
    public UserModel transformToPresentation(Object userDomain, Class presentationClass) {
        if (userDomain != null) {
            User cast = gson.fromJson(gson.toJson(userDomain), User.class);
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
     * Transform a {@link UserRealmModel} into an {@link User}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link User} if valid {@link UserRealmModel} otherwise null.
     */
    public Collection<UserModel> transformAllToPresentation(Collection userRealmModels, Class presentationClass) {
        Collection<UserModel> userModels = new ArrayList<>();
        for (int i = 0; i < userRealmModels.size(); i++)
            userModels.add(transformToPresentation(userRealmModels.toArray()[i], presentationClass));
        return userModels;
    }

    /**
     * Transform a {@link User} into an {@link User}.
     *
     * @param userRealmModel Object to be transformed.
     * @return {@link User} if valid {@link User} otherwise null.
     */
    public User transformToDomain(Object userRealmModel, Class domainClass) {
        if (userRealmModel != null) {
            UserEntity cast = gson.fromJson(gson.toJson(userRealmModel), UserEntity.class);
            User user = new User(cast.getUserId());
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
     * Transform a {@link UserRealmModel} into an {@link User}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link User} if valid {@link UserRealmModel} otherwise null.
     */
    public List<User> transformAllToDomain(Collection userRealmModels, Class domainClass) {
        List<User> users = new ArrayList<>();
        for (Object realmObject : userRealmModels)
            users.add(transformToDomain(realmObject, domainClass));
        return users;
    }

    /**
     * Transform a {@link User} into an {@link User}.
     *
     * @param item Object to be transformed.
     * @return {@link UserRealmModel} if valid {@link User} otherwise null.
     */
    // FIXME: 4/26/16 fix cover url mapping
    // FIXME: 4/26/16 fix id on edit
    public Object transformToRealm(Object item, Class dataClass) {
        if (item != null) {
            UserRealmModel userRealmModel = new UserRealmModel();
            UserRealmModel cast = (UserRealmModel) gson.fromJson(gson.toJson(item), dataClass);
            userRealmModel.setFollowers(cast.getFollowers());
            userRealmModel.setDescription(cast.getDescription());
            userRealmModel.setEmail(cast.getEmail());
            userRealmModel.setCoverUrl(cast.getCoverUrl());
            userRealmModel.setFullName(cast.getFullName());
            if (cast.getUserId() != 0) {
                return userRealmModel.setUserId(cast.getUserId());
//                return userRealmModel;
            } else {
                return userRealmModel.setUserId(Utils.getNextId(UserRealmModel.class,
                        UserRealmModel.ID_COLUMN));
//                return userRealmModel;
            }
        }
        return null;
    }

    public UserRealmModel transformToRealm(User user) {
        if (user != null) {
            UserRealmModel userRealmModel = new UserRealmModel();
            userRealmModel.setFollowers(user.getFollowers());
            userRealmModel.setDescription(user.getDescription());
            userRealmModel.setEmail(user.getEmail());
            userRealmModel.setCoverUrl(user.getCoverUrl());
            userRealmModel.setFullName(user.getFullName());
            if (user.getUserId() != 0)
                return userRealmModel.setUserId(user.getUserId());
            else
                return userRealmModel.setUserId(Utils.getNextId(UserRealmModel.class,
                        UserRealmModel.ID_COLUMN));
        }
        return null;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link User}.
     *
     * @param collection Objects to be transformed.
     * @return {@link List <UserRealmModel>} if valid {@link UserRealmModel} otherwise null.
     */
    public Collection<Object> transformAllToRealm(Collection collection, Class dataClass) {
        List<Object> userRealmModels = new ArrayList<>();
        Object userRealmModel;
        for (Object userEntity : collection) {
            userRealmModel = transformToRealm(userEntity, dataClass);
            if (userRealmModel != null)
                userRealmModels.add(userRealmModel);
        }
        return userRealmModels;
    }
}