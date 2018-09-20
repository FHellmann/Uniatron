package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.os.Build;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.edu.uni.augsburg.uniatron.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OnBoardingActivityTest {

    @Rule
    public ActivityTestRule<OnBoardingActivity> mActivityRule = new ActivityTestRule<>(OnBoardingActivity.class);
    private UiDevice device;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(getInstrumentation());
        assertThat(device, is(notNullValue()));
    }

    @Test
    public void testOnBoarding() throws UiObjectNotFoundException {
        // Intro
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        onView(withText(agency.tango.materialintroscreen.R.string.please_grant_permissions))
                .check((view, noViewFoundException) -> assertThat(view, is(nullValue())));

        // App usage permission
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onView(withText(agency.tango.materialintroscreen.R.string.please_grant_permissions))
                    .check((view, noViewFoundException) -> assertThat(view.getVisibility(), is(View.VISIBLE)));
            onView(withId(agency.tango.materialintroscreen.R.id.button_message)).perform(click());
            device.findObject(new UiSelector().text(getTargetContext().getString(R.string.app_name))).click();
            device.findObject(new UiSelector().checkable(true).checked(false)).click();
            device.pressBack();
            device.pressBack();
            onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        }

        // Battery optimization permission
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onView(withText(agency.tango.materialintroscreen.R.string.please_grant_permissions))
                    .check((view, noViewFoundException) -> assertThat(view.getVisibility(), is(View.VISIBLE)));
            onView(withId(agency.tango.materialintroscreen.R.id.button_message)).perform(click());
            device.findObject(new UiSelector().className(Spinner.class)).click();
            device.findObjects(By.res("android:id/text1")).get(1).click();
            final UiScrollable uiScrollable = new UiScrollable(new UiSelector().className(ListView.class));
            uiScrollable.setMaxSearchSwipes(100);
            uiScrollable.scrollIntoView(new UiSelector().text(getTargetContext().getString(R.string.app_name)));
            device.findObject(new UiSelector().text(getTargetContext().getString(R.string.app_name))).click();
            device.findObject(new UiSelector().checkable(true).checked(false)).click();
            device.findObject(new UiSelector().className(Button.class).index(1)).click();
            device.pressBack();
            onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        }

        // Body sensor permission
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            onView(withText(agency.tango.materialintroscreen.R.string.impassable_slide))
                    .check((view, noViewFoundException) -> assertThat(view.getVisibility(), is(View.VISIBLE)));
            onView(withId(agency.tango.materialintroscreen.R.id.button_message)).perform(click());
            device.findObject(new UiSelector().className(Button.class).index(1)).click();
            onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        }

        // Shopping cart
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
        onView(withText(agency.tango.materialintroscreen.R.string.please_grant_permissions))
                .check((view, noViewFoundException) -> assertThat(view, is(nullValue())));

        // Blacklist
        onView(withId(agency.tango.materialintroscreen.R.id.button_next)).perform(click());
    }
}