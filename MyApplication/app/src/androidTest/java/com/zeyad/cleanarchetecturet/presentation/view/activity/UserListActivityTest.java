package com.zeyad.cleanarchetecturet.presentation.view.activities;

import android.app.Fragment;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.zeyad.cleanarchetecturet.R;

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
        Fragment userListFragment =
                userListActivity.getFragmentManager().findFragmentById(R.id.fragmentUserList);
        assertNotNull(userListFragment);
    }

    public void testContainsProperTitle() {
        String actualTitle = this.userListActivity.getTitle().toString().trim();
        assertEquals(actualTitle, "Users List");
    }

    private Intent createTargetIntent() {
        return UserListActivity.getCallingIntent(getInstrumentation().getTargetContext());
    }
}