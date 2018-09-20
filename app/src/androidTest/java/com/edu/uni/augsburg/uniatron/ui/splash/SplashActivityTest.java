package com.edu.uni.augsburg.uniatron.ui.splash;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.onboarding.OnBoardingActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SplashActivityTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<>(SplashActivity.class, true, false);
    private int splashScreenWaitingTime = 3000;

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void viewSplashFirstTime_NavigateToOnBoardingAfter3000ms() {
        mActivityRule.launchActivity(null);
        AppContext.getInstance(mActivityRule.getActivity().getApplicationContext()).getPreferences().setFirstStart(true);
        IdlingResource idlingResource = new ElapsedTimeIdlingResource(splashScreenWaitingTime);
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(OnBoardingActivity.class.getName()));
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    @Test
    public void viewSplashScreenSecondTime_NavigateToMainScreenAfter3000ms() {
        mActivityRule.launchActivity(null);
        AppContext.getInstance(mActivityRule.getActivity().getApplicationContext()).getPreferences().setFirstStart(false);
        IdlingResource idlingResource = new ElapsedTimeIdlingResource(splashScreenWaitingTime);
        IdlingRegistry.getInstance().register(idlingResource);
        intended(hasComponent(MainActivity.class.getName()));
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}