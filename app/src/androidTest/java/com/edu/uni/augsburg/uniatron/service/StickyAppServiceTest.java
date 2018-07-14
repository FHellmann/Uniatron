package com.edu.uni.augsburg.uniatron.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StickyAppServiceTest {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    private StickyAppService mService;

    @Before
    public void setUp() {
        mService = new StickyAppService();
        final Context context = InstrumentationRegistry.getTargetContext();
        context.startService(new Intent(context, StickyAppService.class));
    }

    @Test
    public void onBind() {
        final IBinder bind = mService.onBind(new Intent());
        assertThat(bind, is(nullValue()));
    }

    @Test
    public void onStartCommand() {
        //assertThat(mService.onStartCommand(new Intent(), 0, 0), is(START_STICKY));
    }
}