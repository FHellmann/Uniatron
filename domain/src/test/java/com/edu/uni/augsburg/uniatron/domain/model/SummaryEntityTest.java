package com.edu.uni.augsburg.uniatron.domain.model;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class SummaryEntityTest {

    @Test(expected = NullPointerException.class)
    public void getDefaultTimestamp() {
        final SummaryEntity entity = new SummaryEntity();
        assertThat(entity.getTimestamp(), is(nullValue()));
    }

    @Test
    public void setAndGetTimestamp() {
        final SummaryEntity entity = new SummaryEntity();
        final Date expected = new Date();
        entity.setTimestamp(expected);
        assertThat(entity.getTimestamp(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void setNullAndGetTimestamp() {
        final SummaryEntity entity = new SummaryEntity();
        entity.setTimestamp(null);
    }

    @Test
    public void getDefaultTime() {
        final SummaryEntity entity = new SummaryEntity();
        assertThat(entity.getAppUsageTime(), is(0L));
    }

    @Test
    public void setAndGetTime() {
        final SummaryEntity entity = new SummaryEntity();
        final long expected = 10L;
        entity.setAppUsageTime(expected);
        assertThat(entity.getAppUsageTime(), is(equalTo(expected)));
    }

    @Test
    public void getDefaultStepCount() {
        final SummaryEntity entity = new SummaryEntity();
        assertThat(entity.getSteps(), is(0L));
    }

    @Test
    public void setAndGetStepCount() {
        final SummaryEntity entity = new SummaryEntity();
        final long expected = 10L;
        entity.setSteps(expected);
        assertThat(entity.getSteps(), is(expected));
    }

    @Test
    public void getDefaultEmotionAvg() {
        final SummaryEntity entity = new SummaryEntity();
        assertThat(entity.getEmotionAvg(), is(0d));
    }

    @Test
    public void setAndGetEmotionAvg() {
        final SummaryEntity entity = new SummaryEntity();
        final double expected = 3.5;
        entity.setEmotionAvg(expected);
        assertThat(entity.getEmotionAvg(), is(expected));
    }
}