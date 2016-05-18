package com.zeyad.cleanarchitecture.presentation.presenters;

import android.content.Context;
import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.GenericListPresenter;
import com.zeyad.cleanarchitecture.presentation.view_models.mapper.UserViewModelDataMapper;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserListView;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Subscriber;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by ZIaDo on 4/30/16.
 */
public class GenericListPresenterTest extends AndroidTestCase {
    private GenericListPresenter userListPresenter;
    @Mock
    private Context mockContext;
    @Mock
    private UserListView mockUserListView;
    @Mock
    private GenericUseCase mockGetUserList;
    @Mock
    private UserViewModelDataMapper mockUserViewModelDataMapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        userListPresenter = new GenericListPresenter(mockGetUserList);
        userListPresenter.setView(mockUserListView);
    }

    public void testUserListPresenterInitialize() {
        given(mockUserListView.getContext()).willReturn(mockContext);

        userListPresenter.initialize();

        verify(mockUserListView).hideRetry();
        verify(mockUserListView).showLoading();
        verify(mockGetUserList).execute(any(Subscriber.class));
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