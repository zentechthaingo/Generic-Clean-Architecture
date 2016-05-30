package com.zeyad.cleanarchitecture.presentation.presenters;

import android.content.Context;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.screens.GenericListView;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserListPresenter;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserViewHolder;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.presentation.view_models.mapper.UserViewModelDataMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created by ZIaDo on 4/30/16.
 */
public class GenericListPresenterTest {
    private UserListPresenter userListPresenter;
    @Mock
    private Context mockContext;
    @Mock
    private GenericListView<UserViewModel, UserViewHolder> mockUserListView;
    @Mock
    private GenericUseCase mockGetUserList;
    @Mock
    private UserViewModelDataMapper mockUserViewModelDataMapper;
    private Class presentationClass, domainClass, dataClass;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userListPresenter = new UserListPresenter(mockGetUserList);
        userListPresenter.setView(mockUserListView);
        presentationClass = UserViewModel.class;
        domainClass = User.class;
        dataClass = UserRealmModel.class;
    }

    @Test
    public void testUserListPresenterInitialize() {
        given(mockUserListView.getContext()).willReturn(mockContext);

        userListPresenter.initialize();

        verify(mockUserListView).hideRetry();
        verify(mockUserListView).showLoading();
        verify(mockGetUserList).executeList(new DefaultSubscriber<>(), "", presentationClass,
                domainClass, dataClass, true);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSetView() throws Exception {

    }

    @Test
    public void testResume() throws Exception {

    }

    @Test
    public void testPause() throws Exception {

    }

    @Test
    public void testDestroy() throws Exception {

    }

    @Test
    public void testInitialize() throws Exception {

    }

    @Test
    public void testOnUserClicked() throws Exception {

    }

    @Test
    public void testSearch() throws Exception {

    }

    @Test
    public void testDeleteCollection() throws Exception {

    }

    @Test
    public void testShowUsersCollectionInView() throws Exception {

    }

    @Test
    public void testGetmUserModels() throws Exception {

    }
}