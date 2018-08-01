package com.edu.uni.augsburg.uniatron.domain.table;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AppUsageEntityTest {

    @Test
    public void getDefaultId() {
        final AppUsageEntity entity = new AppUsageEntity();
        assertThat(entity.getId(), is(0L));
    }

    @Test
    public void setAndGetId() {
        final AppUsageEntity entity = new AppUsageEntity();
        final long expected = 10L;
        entity.setId(expected);
        assertThat(entity.getId(), is(equalTo(expected)));
    }

    @Test
    public void getDefaultAppName() {
        final AppUsageEntity entity = new AppUsageEntity();
        assertThat(entity.getPackageName(), is(nullValue()));
    }

    @Test
    public void setAndGetAppName() {
        final AppUsageEntity entity = new AppUsageEntity();
        final String expected = "Test";
        entity.setPackageName(expected);
        assertThat(entity.getPackageName(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void getDefaultTimestamp() {
        final AppUsageEntity entity = new AppUsageEntity();
        assertThat(entity.getTimestamp(), is(nullValue()));
    }

    @Test
    public void setAndGetTimestamp() {
        final AppUsageEntity entity = new AppUsageEntity();
        final Date expected = new Date();
        entity.setTimestamp(expected);
        assertThat(entity.getTimestamp(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void setNullAndGetTimestamp() {
        final AppUsageEntity entity = new AppUsageEntity();
        entity.setTimestamp(null);
    }

    @Test
    public void getDefaultTime() {
        final AppUsageEntity entity = new AppUsageEntity();
        assertThat(entity.getUsageTime(), is(0L));
    }

    @Test
    public void setAndGetTime() {
        final AppUsageEntity entity = new AppUsageEntity();
        final long expected = 10L;
        entity.setUsageTime(expected);
        assertThat(entity.getUsageTime(), is(equalTo(expected)));
    }

    @Test
    public void getDefaultUsageTimeAllPercent() {
        final AppUsageEntity entity = new AppUsageEntity();
        assertThat(entity.getUsageTimeAllPercent(), is(0D));
    }

    @Test
    public void setAndGetUsageTimeAllPercent() {
        final AppUsageEntity entity = new AppUsageEntity();
        final double expected = 0.73;
        entity.setUsageTimeAllPercent(expected);
        assertThat(entity.getUsageTimeAllPercent(), is(equalTo(expected)));
    }
}