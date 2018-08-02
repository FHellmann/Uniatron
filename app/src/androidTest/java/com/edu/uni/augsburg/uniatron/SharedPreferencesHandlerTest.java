package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SharedPreferencesHandlerTest {

    private SharedPreferencesHandler mHandler;
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        mHandler = new SharedPreferencesHandler(mContext);
    }

    @Test
    public void testBlacklistEmpty() {
        assertThat(mHandler.getAppsBlacklist(), is(notNullValue()));
        assertThat(mHandler.getAppsBlacklist().size(), is(0));
    }

    @Test
    public void testBlacklist() {
        final String packageName = "com.test.app";
        mHandler.addAppToBlacklist(packageName);
        assertThat(mHandler.getAppsBlacklist(), hasItems(packageName));
        mHandler.removeAppFromBlacklist(packageName);
        assertThat(mHandler.getAppsBlacklist().size(), is(0));
    }

    @Test
    public void testStepFactorInitial() {
        assertThat(mHandler.getStepsFactor(), is(1.0));
    }

    @Test
    public void testStepFactor() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        preferences.edit().putString("pref_fitness_level", "2").commit();

        assertThat(mHandler.getStepsFactor(), is(2.0));
    }

    @Test
    public void testFirstStartTrue() {
        assertThat(mHandler.isFirstStart(), is(true));
    }

    @Test
    public void testFirstStartFalse() {
        mHandler.setFirstStartDone();
        assertThat(mHandler.isFirstStart(), is(false));
    }

    @Test
    public void testIntroBonusTrue() {
        assertThat(mHandler.isIntroBonusEligible(), is(true));
    }

    @Test
    public void testIntroBonusFalse() {
        mHandler.setIntroBonusGranted();
        assertThat(mHandler.isIntroBonusEligible(), is(false));
    }
}