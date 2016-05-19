package com.zeyad.cleanarchitecture.presentation.presenters;

import android.content.Context;
import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.presentation.view_models.mapper.UserViewModelDataMapper;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Subscriber;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class UserDetailsBasePresenterTest extends AndroidTestCase {

    private static final int FAKE_USER_ID = 123;

    private UserDetailsPresenter userDetailsPresenter;

    @Mock
    private Context mockContext;
    @Mock
    private UserDetailsView mockUserDetailsView;
    @Mock
    private GetUserDetails mockGetUserDetails;
    @Mock
    private UserViewModelDataMapper mockUserViewModelDataMapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        userDetailsPresenter = new UserDetailsPresenter(mockGetUserDetails,
                mockUserViewModelDataMapper);
        userDetailsPresenter.setView(mockUserDetailsView);
    }

    public void testUserDetailsPresenterInitialize() {
        given(mockUserDetailsView.getContext()).willReturn(mockContext);

        userDetailsPresenter.initialize(FAKE_USER_ID);

        verify(mockUserDetailsView).hideRetry();
        verify(mockUserDetailsView).showLoading();
        verify(mockGetUserDetails).execute(any(Subscriber.class));
    }
}