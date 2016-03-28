package com.zeyad.cleanarchetecture.presentation.exception;

import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.data.exceptions.NetworkConnectionException;
import com.zeyad.cleanarchitecture.data.exceptions.UserNotFoundException;
import com.zeyad.cleanarchitecture.presentation.exception.ErrorMessageFactory;


public class ErrorMessageFactoryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testNetworkConnectionErrorMessage() {
        String expectedMessage = getContext().getString(R.string.exception_message_no_connection);
        String actualMessage = ErrorMessageFactory.create(getContext(),
                new NetworkConnectionException());
        assertEquals(actualMessage, expectedMessage);
    }

    public void testUserNotFoundErrorMessage() {
        String expectedMessage = getContext().getString(R.string.exception_message_user_not_found);
        String actualMessage = ErrorMessageFactory.create(getContext(), new UserNotFoundException());
        assertEquals(actualMessage, expectedMessage);
    }
}