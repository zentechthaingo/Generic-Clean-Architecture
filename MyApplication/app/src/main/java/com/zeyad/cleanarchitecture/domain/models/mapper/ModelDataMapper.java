package com.zeyad.cleanarchitecture.domain.models.mapper;

import com.google.gson.Gson;
import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ModelDataMapper {
    private Gson gson;

    @Inject
    public ModelDataMapper() {
        gson = new Gson();
    }

    /**
     * Transform an Object into another Object.
     *
     * @param object Object to be transformed.
     * @return {@link User} if valid {@link User} otherwise null.
     */
    public Object transformToPresentation(Object object, Class presentationClass) {
        if (object != null)
            if (!(object instanceof Boolean))
                return gson.fromJson(gson.toJson(object), presentationClass);
        return null;
    }

    /**
     * Transform a {@link UserRealmModel} into an {@link User}.
     *
     * @param list Objects to be transformed.
     * @return {@link User} if valid {@link UserRealmModel} otherwise null.
     */
    public List transformAllToPresentation(List list, Class presentationClass) {
        List transformedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++)
            transformedList.add(transformToPresentation(list.get(i), presentationClass));
        return transformedList;
    }
}