package com.zeyad.cleanarchitecture.presentation.view_models.mapper;

import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Mapper class used to transformToDomain {@link User} (in the domain layer) to {@link UserViewModel} in the
 * presentation layer.
 */
@PerActivity
public class UserViewModelDataMapper {

    @Inject
    public UserViewModelDataMapper() {
    }

    /**
     * Transform a {@link User} into an {@link UserViewModel}.
     *
     * @param user Object to be transformed.
     * @return {@link UserViewModel}.
     */
    public UserViewModel transform(User user) {
        if (user == null)
            throw new IllegalArgumentException("Cannot transformToDomain a null value");
        UserViewModel userViewModel = new UserViewModel(user.getUserId());
        userViewModel.setCoverUrl(user.getCoverUrl());
        userViewModel.setFullName(user.getFullName());
        userViewModel.setEmail(user.getEmail());
        userViewModel.setDescription(user.getDescription());
        userViewModel.setFollowers(user.getFollowers());
        return userViewModel;
    }

    /**
     * Transform a collection of {@link User} into a collection of {@link UserViewModel}.
     *
     * @param usersCollection Objects to be transformed.
     * @return List of {@link UserViewModel}.
     */
    public List<UserViewModel> transform(List<User> usersCollection) {
        List<UserViewModel> userViewModelsCollection;
        if (usersCollection != null && !usersCollection.isEmpty()) {
            userViewModelsCollection = new ArrayList<>();
            for (User user : usersCollection)
                userViewModelsCollection.add(transform(user));
        } else
            userViewModelsCollection = Collections.emptyList();
        return userViewModelsCollection;
    }
}