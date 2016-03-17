package com.zeyad.cleanarchitecturet.data.entities.mapper;

import com.zeyad.cleanarchitecturet.data.entities.UserEntity;
import com.zeyad.cleanarchitecturet.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecturet.domain.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmObject;

@Singleton
public class EntityDataMapper<R extends RealmObject, T> {

    @Inject
    public EntityDataMapper() {
    }

    /**
     * Transform a {@link UserEntity} into an {@link User}.
     *
     * @param userRealmModel Object to be transformed.
     * @return {@link User} if valid {@link UserEntity} otherwise null.
     */
    public T transform(R userRealmModel) {
        if (userRealmModel != null && userRealmModel instanceof UserRealmModel) {
            UserRealmModel cast = ((UserRealmModel) userRealmModel);
            User user = new User(cast.getUserId());
            user.setCoverUrl(cast.getCoverUrl());
            user.setFullName(cast.getFullName());
            user.setDescription(cast.getDescription());
            user.setFollowers(cast.getFollowers());
            user.setEmail(cast.getEmail());
            return (T) user;
        }
        return null;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link UserEntity}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link UserEntity} if valid {@link UserRealmModel} otherwise null.
     */
    public List<T> transformAll(Collection<R> userRealmModels) {
        List<T> userEntityList = new ArrayList<>();
        T userEntity;
        for (R userRealmModel : userRealmModels) {
            userEntity = transform(userRealmModel);
            if (userEntity != null)
                userEntityList.add(userEntity);
        }
        return userEntityList;
    }
}