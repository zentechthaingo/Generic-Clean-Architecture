package com.zeyad.cleanarchitecture.presentation.screens.users.list;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.data.repository.datastore.DataStore;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.screens.GenericListExtendedPresenter;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.utilities.Constants;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;

@PerActivity
public class UserListPresenter extends GenericListExtendedPresenter<UserViewModel, UserViewHolder> {

    @Inject
    public UserListPresenter(GenericUseCase getUserListUserCase) {
        super(getUserListUserCase);
    }

    @Override
    public void getItemList() {
        mGetGenericListUseCase.executeList(new ItemListSubscriber(), Constants.API_BASE_URL + "users.json",
                UserViewModel.class, User.class, UserRealmModel.class, true);
    }

    @Override
    public void search(String query) {
        mGetGenericListUseCase.executeSearch(new SearchSubscriber(), query, UserRealmModel.FULL_NAME_COLUMN,
                UserViewModel.class, User.class, UserRealmModel.class);
        mGetGenericListUseCase.executeSearch(new SearchSubscriber(), Realm.getDefaultInstance()
                        .where(UserRealmModel.class).contains(UserRealmModel.FULL_NAME_COLUMN, query),
                UserViewModel.class, User.class);
    }

    @Override
    public void deleteCollection(List<Long> ids) {
        HashMap<String, Object> keyValuePairs = new HashMap<>(1);
        keyValuePairs.put(DataStore.IDS, ids);
        mGetGenericListUseCase.executeDeleteCollection(new DeleteSubscriber(), "", keyValuePairs,
                User.class, UserRealmModel.class, true);
    }
}