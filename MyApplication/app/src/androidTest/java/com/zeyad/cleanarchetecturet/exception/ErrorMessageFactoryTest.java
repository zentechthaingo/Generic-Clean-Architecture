package com.zeyad.cleanarchetecturet.exception;

import android.test.AndroidTestCase;

import com.zeyad.cleanarchetecturet.R;
import com.zeyad.cleanarchetecturet.data.exceptions.NetworkConnectionException;
import com.zeyad.cleanarchetecturet.data.exceptions.UserNotFoundException;
import com.zeyad.cleanarchetecturet.presentation.exception.ErrorMessageFactory;


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