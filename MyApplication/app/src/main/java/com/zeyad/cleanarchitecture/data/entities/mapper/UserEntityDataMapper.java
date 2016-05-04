package com.zeyad.cleanarchitecture.data.entities.mapper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.zeyad.cleanarchitecture.data.entities.UserEntity;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmObject;

@Singleton
public class UserEntityDataMapper extends EntityDataMapper {

    @Inject
    public UserEntityDataMapper() {
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
     * Transform a {@link UserEntity} into an {@link User}.
     *
     * @param userEntity Object to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public User transform(UserEntity userEntity) {
        User user = null;
        if (userEntity != null) {
            user = new User(userEntity.getUserId());
            user.setCoverUrl(userEntity.getCoverUrl());
            user.setFullName(userEntity.getFullName());
            user.setDescription(userEntity.getDescription());
            user.setFollowers(userEntity.getFollowers());
            user.setEmail(userEntity.getEmail());
        }

        return user;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param userEntities Objects to be transformed.
     * @return {@link UserEntity} if valid {@link UserRealmModel} otherwise null.
     */
    public List<User> transformAll(Collection<UserEntity> userEntities) {
        List<User> userList = new ArrayList<>();
        User user;
        for (UserEntity userEntity : userEntities) {
            user = transform(userEntity);
            if (user != null)
                userList.add(user);
        }
        return userList;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link UserEntity} if valid {@link UserRealmModel} otherwise null.
     */
    public List<UserEntity> transformAllFromRealm(Collection<UserRealmModel> userRealmModels) {
        List<UserEntity> userEntityList = new ArrayList<>();
        UserEntity userEntity;
        for (UserRealmModel userRealmModel : userRealmModels) {
            userEntity = transform(userRealmModel);
            if (userEntity != null)
                userEntityList.add(userEntity);
        }
        return userEntityList;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param userEntities Objects to be transformed.
     * @return {@link List<UserRealmModel>} if valid {@link UserRealmModel} otherwise null.
     */
    public List<UserRealmModel> transformAllToRealm(Collection<UserEntity> userEntities) {
        List<UserRealmModel> userRealmModels = new ArrayList<>();
        UserRealmModel userRealmModel;
        for (UserEntity userEntity : userEntities) {
            userRealmModel = transformToRealm(userEntity);
            if (userRealmModel != null)
                userRealmModels.add(userRealmModel);
        }
        return userRealmModels;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param userRealmModel Object to be transformed.
     * @return {@link UserEntity} if valid {@link UserRealmModel} otherwise null.
     */
    public UserEntity transform(UserRealmModel userRealmModel) {
        UserEntity userEntity = null;
        if (userRealmModel != null) {
            userEntity = new UserEntity();
            userEntity.setUserId(userRealmModel.getUserId());
            userEntity.setCoverUrl(userRealmModel.getCover_url());
            userEntity.setFullName(userRealmModel.getFullName());
            userEntity.setDescription(userRealmModel.getDescription());
            userEntity.setFollowers(userRealmModel.getFollowers());
            userEntity.setEmail(userRealmModel.getEmail());
        }
        return userEntity;
    }

    public UserRealmModel transformToRealm(UserEntity userEntity) {
        UserRealmModel userRealmModel = null;
        if (userEntity != null) {
            userRealmModel = new UserRealmModel();
            if (userEntity.getUserId() != 0)
                userRealmModel.setUserId(userEntity.getUserId());
            userRealmModel.setCover_url(userEntity.getCoverUrl());
            userRealmModel.setFullName(userEntity.getFullName());
            userRealmModel.setDescription(userEntity.getDescription());
            userRealmModel.setFollowers(userEntity.getFollowers());
            userRealmModel.setEmail(userEntity.getEmail());
        }
        return userRealmModel;
    }

    /**
     * Transform a List of {@link UserEntity} into a collection of {@link User}.
     *
     * @param userEntityCollection Object collection to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public List<User> transform(Collection<UserEntity> userEntityCollection) {
        List<User> userList = new ArrayList<>(20);
        User user;
        for (UserEntity userEntity : userEntityCollection) {
            user = transform(userEntity);
            if (user != null)
                userList.add(user);
        }
        return userList;
    }

    /**
     * Transform a {@link User} into an {@link User}.
     *
     * @param userRealmModel Object to be transformed.
     * @return {@link User} if valid {@link User} otherwise null.
     */
    public User transformToDomain(UserRealmModel userRealmModel) {
        if (userRealmModel != null) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            User user = new User(userRealmModel.getUserId());
            user.setCoverUrl(userRealmModel.getCover_url());
            user.setFullName(userRealmModel.getFullName());
            user.setDescription(userRealmModel.getDescription());
            user.setFollowers(userRealmModel.getFollowers());
            user.setEmail(userRealmModel.getEmail());
            realm.commitTransaction();
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
    public List<User> transformAllToDomain(Collection<UserRealmModel> userRealmModels) {
        List<User> users = new ArrayList<>();
        for (UserRealmModel realmObject : userRealmModels)
            users.add(transformToDomain(realmObject));
        return users;
    }
}