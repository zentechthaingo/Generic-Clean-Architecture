package com.zeyad.cleanarchitecture.presentation.presenters;

import android.content.Context;
import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.data.entities.UserRealmModel;
import com.zeyad.cleanarchitecture.domain.interactors.DefaultSubscriber;
import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.domain.models.User;
import com.zeyad.cleanarchitecture.presentation.screens.GenericEditableItemView;
import com.zeyad.cleanarchitecture.presentation.screens.users.details.UserDetailPresenter;
import com.zeyad.cleanarchitecture.presentation.view_models.UserViewModel;
import com.zeyad.cleanarchitecture.presentation.view_models.mapper.UserViewModelDataMapper;

import org.junit.After;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class GenericDetailPresenterTest extends AndroidTestCase {

    private static final int FAKE_USER_ID = 123;

    private UserDetailPresenter userDetailsPresenter;

    @Mock
    private Context mockContext;
    @Mock
    private GenericEditableItemView<UserViewModel> mockUserDetailsView;
    @Mock
    private GenericUseCase mockGetUserDetails;
    @Mock
    private UserViewModelDataMapper mockUserViewModelDataMapper;
    private Class presentationClass, domainClass, dataClass;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        userDetailsPresenter = new UserDetailPresenter(mockGetUserDetails);
        userDetailsPresenter.setView(mockUserDetailsView);
        presentationClass = UserViewModel.class;
        domainClass = User.class;
        dataClass = UserRealmModel.class;
    }

    public void testUserDetailsPresenterInitialize() {
        given(mockUserDetailsView.getContext()).willReturn(mockContext);

        userDetailsPresenter.initialize(FAKE_USER_ID);

        verify(mockUserDetailsView).hideRetry();
        verify(mockUserDetailsView).showLoading();
        verify(mockGetUserDetails).executeGetObject(new DefaultSubscriber<>(), "", "", -1,
                presentationClass, domainClass, dataClass, true);
    }

    @After
    public void tearDown() throws Exception {

    }

    public void testSetView() throws Exception {

    }

    public void testResume() throws Exception {

    }

    public void testPause() throws Exception {

    }

    public void testDestroy() throws Exception {

    }

    public void testInitialize() throws Exception {

    }

    public void testSetupEdit() throws Exception {

    }

    public void testSubmitEdit() throws Exception {

    }
}