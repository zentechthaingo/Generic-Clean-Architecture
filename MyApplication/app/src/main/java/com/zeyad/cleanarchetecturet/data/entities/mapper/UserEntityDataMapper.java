package com.zeyad.cleanarchetecturet.data.entities.mapper;

import com.zeyad.cleanarchetecturet.data.entities.UserEntity;
import com.zeyad.cleanarchetecturet.data.entities.UserRealmModel;
import com.zeyad.cleanarchetecturet.domain.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserEntityDataMapper {

    @Inject
    public UserEntityDataMapper() {
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
            user.setFullName(userEntity.getFullname());
            user.setDescription(userEntity.getDescription());
            user.setFollowers(userEntity.getFollowers());
            user.setEmail(userEntity.getEmail());
        }

        return user;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link UserEntity} if valid {@link UserRealmModel} otherwise null.
     */
    public List<UserEntity> transformAll(Collection<UserRealmModel> userRealmModels) {
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
     * @param userRealmModel Object to be transformed.
     * @return {@link UserEntity} if valid {@link UserRealmModel} otherwise null.
     */
    public UserEntity transform(UserRealmModel userRealmModel) {
        UserEntity userEntity = null;
        if (userRealmModel != null) {
            userEntity = new UserEntity();
            userEntity.setUserId(userRealmModel.getUserId());
            userEntity.setCoverUrl(userRealmModel.getCoverUrl());
            userEntity.setFullname(userRealmModel.getFullName());
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
            userRealmModel.setUserId(userEntity.getUserId());
            userRealmModel.setCoverUrl(userEntity.getCoverUrl());
            userRealmModel.setFullName(userEntity.getFullname());
            userRealmModel.setDescription(userEntity.getDescription());
            userRealmModel.setFollowers(userEntity.getFollowers());
            userRealmModel.setEmail(userEntity.getEmail());
        }
        return userRealmModel;
    }

    /**
     * Transform a List of {@link UserEntity} into a Collection of {@link User}.
     *
     * @param userEntityCollection Object Collection to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public List<User> transform(Collection<UserEntity> userEntityCollection) {
        List<User> userList = new ArrayList<>(20);
        User user;
        for (UserEntity userEntity : userEntityCollection) {
            user = transform(userEntity);
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }
}