package com.zeyad.cleanarchitecture.presentation.presenters;

import android.content.Context;
import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.domain.interactors.GenericUseCase;
import com.zeyad.cleanarchitecture.presentation.view_models.mapper.UserViewModelDataMapper;
import com.zeyad.cleanarchitecture.presentation.views.UserDetailsView;

import org.junit.After;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Subscriber;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class GenericDetailPresenterTest extends AndroidTestCase {

    private static final int FAKE_USER_ID = 123;

    private UserDetailsPresenter userDetailsPresenter;

    @Mock
    private Context mockContext;
    @Mock
    private UserDetailsView mockUserDetailsView;
    @Mock
    private GenericUseCase mockGetUserDetails;
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