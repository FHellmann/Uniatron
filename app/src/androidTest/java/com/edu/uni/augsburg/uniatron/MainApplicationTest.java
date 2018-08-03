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
        assertThat(MainApplication.getInstance(mContext).getSharedPreferencesHandler(), is(notNullValue()));
    }

    @Test
    public void getAppUsageDao() {
        assertThat(MainApplication.getInstance(mContext).getAppUsageDao(), is(notNullValue()));
    }

    @Test
    public void getEmotionDao() {
        assertThat(MainApplication.getInstance(mContext).getEmotionDao(), is(notNullValue()));
    }

    @Test
    public void getStepCountDao() {
        assertThat(MainApplication.getInstance(mContext).getStepCountDao(), is(notNullValue()));
    }

    @Test
    public void getSummaryDao() {
        assertThat(MainApplication.getInstance(mContext).getSummaryDao(), is(notNullValue()));
    }

    @Test
    public void getTimeCreditDao() {
        assertThat(MainApplication.getInstance(mContext).getTimeCreditDao(), is(notNullValue()));
    }
}