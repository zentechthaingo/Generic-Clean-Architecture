package com.zeyad.cleanarchitecturet.presentation.exception;

import android.test.AndroidTestCase;

import com.zeyad.cleanarchitecturet.R;
import com.zeyad.cleanarchitecturet.data.exceptions.NetworkConnectionException;
import com.zeyad.cleanarchitecturet.data.exceptions.UserNotFoundException;


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