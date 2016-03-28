package com.zeyad.cleanarchetecture.presentation.view.activity;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.views.activities.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class UserDetailsActivityTest extends ActivityInstrumentationTestCase2<UserDetailsActivity> {

    private static final int FAKE_USER_ID = 10;
    private UserDetailsActivity userDetailsActivity;

    public UserDetailsActivityTest() {
        super(UserDetailsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.setActivityIntent(createTargetIntent());
        this.userDetailsActivity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testContainsUserDetailsFragment() {
        assertNotNull(userDetailsActivity.getFragmentManager().findFragmentById(R.id.fl_fragment));
    }

    public void testContainsProperTitle() {
        assertEquals(userDetailsActivity.getTitle().toString().trim(), "User Details");
    }

    public void testLoadUserHappyCaseViews() {
        onView(withId(R.id.rl_retry)).check(matches(not(isDisplayed())));
        onView(withId(R.id.rl_progress)).check(matches(not(isDisplayed())));

        onView(withId(R.id.tv_fullname)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_email)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_description)).check(matches(isDisplayed()));
    }

    public void testLoadUserHappyCaseData() {
        onView(withId(R.id.tv_fullname)).check(matches(withText("John Sanchez")));
        onView(withId(R.id.tv_email)).check(matches(withText("dmedina@katz.edu")));
        onView(withId(R.id.tv_followers)).check(matches(withText("4523")));
    }

    private Intent createTargetIntent() {
        return UserDetailsActivity.getCallingIntent(getInstrumentation().getTargetContext(), FAKE_USER_ID);
    }
}