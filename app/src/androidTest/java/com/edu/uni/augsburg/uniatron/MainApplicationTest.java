package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MainApplicationTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void getSharedPreferencesHandler() {
        assertThat(MainApplication.getSharedPreferencesHandler(mContext), is(notNullValue()));
    }

    @Test
    public void getAppUsageDao() {
        assertThat(MainApplication.getAppUsageDao(mContext), is(notNullValue()));
    }

    @Test
    public void getEmotionDao() {
        assertThat(MainApplication.getEmotionDao(mContext), is(notNullValue()));
    }

    @Test
    public void getStepCountDao() {
        assertThat(MainApplication.getStepCountDao(mContext), is(notNullValue()));
    }

    @Test
    public void getSummaryDao() {
        assertThat(MainApplication.getSummaryDao(mContext), is(notNullValue()));
    }

    @Test
    public void getTimeCreditDao() {
        assertThat(MainApplication.getTimeCreditDao(mContext), is(notNullValue()));
    }
}