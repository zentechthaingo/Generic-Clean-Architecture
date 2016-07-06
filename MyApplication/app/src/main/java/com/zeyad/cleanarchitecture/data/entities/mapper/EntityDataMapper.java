package com.zeyad.cleanarchitecture.data.entities.mapper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.RealmObject;

@Singleton
public class EntityDataMapper implements EntityMapper<Object, Object> {
    private Gson gson;

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

    @Override
    public Object transformToRealm(Object item, Class dataClass) {
        return gson.fromJson(gson.toJson(item), dataClass);
    }

    @Override
    public List transformAllToRealm(List list, Class dataClass) {
        List<Object> objects = new ArrayList<>();
        Object object;
        for (Object item : list) {
            object = transformToRealm(item, dataClass);
            if (object != null)
                objects.add(object);
        }
        return objects;
    }

    @Override
    public Object transformToDomain(Object tenderoRealmModel) {
        return null;
    }

    @Override
    public List<Object> transformAllToDomain(List<Object> tenderoRealmModels) {
        return null;
    }

    /**
     * Transform an Entity into an Model.
     *
     * @param object Object to be transformed.
     * @return Model if valid Entity otherwise null.
     */
    @Override
    public Object transformToDomain(Object object, Class domainClass) {
        if (object != null)
            return domainClass.cast(gson.fromJson(gson.toJson(object), domainClass));
        return null;
    }

    /**
     * Transform a {Entity} into an {Model}.
     *
     * @param list Objects to be transformed.
     * @return {Model} if valid {Entity} otherwise null.
     */
    @Override
    public List<Object> transformAllToDomain(List list, Class domainClass) {
        List domainObjects = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            domainObjects.add(transformToDomain(list.get(i), domainClass));
        return domainObjects;
    }
}