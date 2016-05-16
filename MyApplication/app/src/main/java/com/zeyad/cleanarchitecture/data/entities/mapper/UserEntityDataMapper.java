package com.zeyad.cleanarchitecture.data.entities.mapper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmObject;

@Singleton
public class UserEntityDataMapper implements EntityMapper {
    protected Gson gson;

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
     * Transform a {@link User} into an {@link User}.
     *
     * @param userRealmModel Object to be transformed.
     * @return {@link User} if valid {@link User} otherwise null.
     */
    @Override
    public Object transformToDomain(Object userRealmModel, Class domainClass) {
        if (userRealmModel != null)
            return gson.fromJson(gson.toJson(userRealmModel), domainClass);
        return null;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link User}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link User} if valid {@link UserRealmModel} otherwise null.
     */
    @Override
    public List<Object> transformAllToDomain(List userRealmModels, Class domainClass) {
        List<Object> users = new ArrayList<>();
        for (Object realmObject : userRealmModels)
            users.add(transformToDomain(realmObject, domainClass));
        return users;
    }

    /**
     * Transform a {@link User} into an {@link User}.
     *
     * @param object Object to be transformed.
     * @return {@link User} if valid {@link User} otherwise null.
     */
    @Override
    public User transformToDomain(Object object) {
        if (object != null) {
            Realm realm = Realm.getDefaultInstance();
            try {
                UserRealmModel userRealmModel = (UserRealmModel) object;
                realm.beginTransaction();
                User user = new User(userRealmModel.getUserId());
                user.setCoverUrl(userRealmModel.getCover_url());
                user.setFullName(userRealmModel.getFullName());
                user.setDescription(userRealmModel.getDescription());
                user.setFollowers(userRealmModel.getFollowers());
                user.setEmail(userRealmModel.getEmail());
                realm.commitTransaction();
                realm.close();
                return user;
            } catch (Exception e) {
                e.printStackTrace();
                realm.commitTransaction();
            } finally {
                if (realm.isInTransaction())
                    realm.commitTransaction();
            }
        }
        return new User(0);
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link User}.
     *
     * @param userRealmModels Objects to be transformed.
     * @return {@link User} if valid {@link UserRealmModel} otherwise null.
     */
    @Override
    public List<User> transformAllToDomain(List userRealmModels) {
        List<User> users = new ArrayList<>();
        User user;
        for (Object realmObject : userRealmModels) {
            user = transformToDomain(realmObject);
            if (user.getUserId() != 0)
                users.add(user);
        }
        return users;
    }

    /**
     * Transform a {@link User} into an {@link User}.
     *
     * @param item Object to be transformed.
     * @return {@link UserRealmModel} if valid {@link User} otherwise null.
     */
    @Override
    public Object transformToRealm(Object item, Class dataClass) {
        if (item != null) {
            UserRealmModel userRealmModel = new UserRealmModel();
            UserRealmModel cast = (UserRealmModel) gson.fromJson(gson.toJson(item), dataClass);
            userRealmModel.setFollowers(cast.getFollowers());
            userRealmModel.setDescription(cast.getDescription());
            userRealmModel.setEmail(cast.getEmail());
            userRealmModel.setCover_url(cast.getCover_url());
            userRealmModel.setFullName(cast.getFullName());
            if (cast.getUserId() != 0) {
                return userRealmModel.setUserId(cast.getUserId());
            } else
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
    @Override
    public List<Object> transformAllToRealm(List collection, Class dataClass) {
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