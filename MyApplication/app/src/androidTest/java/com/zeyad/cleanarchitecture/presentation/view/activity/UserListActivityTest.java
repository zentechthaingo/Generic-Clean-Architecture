package com.zeyad.cleanarchitecture.presentation.view.activity;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.screens.users.list.UserListActivity;

public class UserListActivityTest extends ActivityInstrumentationTestCase2<UserListActivity> {

    private UserListActivity userListActivity;

    public UserListActivityTest() {
        super(UserListActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.setActivityIntent(createTargetIntent());
        userListActivity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testContainsUserListFragment() {
        assertNotNull(userListActivity.findViewById(R.id.rv_users));
    }

    public void testContainsProperTitle() {
        String actualTitle = this.userListActivity.getTitle().toString().trim();
        assertEquals(actualTitle, "Users List");
    }

    private Intent createTargetIntent() {
        return UserListActivity.getCallingIntent(getInstrumentation().getTargetContext());
    }
}