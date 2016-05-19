package com.zeyad.cleanarchitecture.presentation.screens.users.list;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.internal.di.PerActivity;
import com.zeyad.cleanarchitecture.presentation.screens.GenericListExtendedPresenter;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.utilities.Constants;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

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

    //    public void search(SearchView searchView) {
//        RxSearchView.queryTextChanges(searchView)
//                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
//                .throttleLast(100, TimeUnit.MILLISECONDS)
//                .debounce(200, TimeUnit.MILLISECONDS)
//                .onBackpressureLatest()
//                .flatMap(query -> mGetGenericListUseCase.executeSearch(query.toString(),
//                        UserRealmModel.FULL_NAME_COLUMN, UserViewModel.class, User.class, UserRealmModel.class))
////                        .distinct()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .onErrorResumeNext(Observable.empty())
//                .subscribe(new SearchSubscriber());
//    }
    @Override
    public void search(String query) {
        mGetGenericListUseCase.executeSearch(query, UserRealmModel.FULL_NAME_COLUMN, new SearchSubscriber(),
                UserViewModel.class, User.class, UserRealmModel.class);
    }

    @Override
    public void deleteCollection(List<Integer> ids) {
        HashMap<String, Object> keyValuePairs = new HashMap<>(1);
        keyValuePairs.put("ids", ids);
        mGetGenericListUseCase.executeDeleteCollection(new DeleteSubscriber(), "", keyValuePairs,
                User.class, UserRealmModel.class, true);
    }
}