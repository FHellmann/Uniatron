package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OnBoardingActivityTest {

    @Rule
    public ActivityTestRule<OnBoardingActivity> mActivityRule = new ActivityTestRule<>(OnBoardingActivity.class);

    @Test
    public void testOnBoarding() {
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
    }
}