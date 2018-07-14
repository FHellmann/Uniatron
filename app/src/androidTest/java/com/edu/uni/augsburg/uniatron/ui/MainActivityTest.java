package com.edu.uni.augsburg.uniatron.ui;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onPrevClicked() {
    }

    @Test
    public void onDateDisplayClicked() {
    }

    @Test
    public void onNextClicked() {
    }

    @Test
    public void onFabClicked() {
    }

    @Test
    public void onMenuItemClick() {
    }
}