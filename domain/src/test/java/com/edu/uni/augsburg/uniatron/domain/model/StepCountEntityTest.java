package com.edu.uni.augsburg.uniatron.domain.model;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class StepCountEntityTest {

    @Test
    public void getDefaultId() {
        final StepCountEntity entity = new StepCountEntity();
        assertThat(entity.getId(), is(0L));
    }

    @Test
    public void setAndGetId() {
        final StepCountEntity entity = new StepCountEntity();
        final long expected = 10L;
        entity.setId(expected);
        assertThat(entity.getId(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void getDefaultTimestamp() {
        final StepCountEntity entity = new StepCountEntity();
        assertThat(entity.getTimestamp(), is(nullValue()));
    }

    @Test
    public void setAndGetTimestamp() {
        final StepCountEntity entity = new StepCountEntity();
        final Date expected = new Date();
        entity.setTimestamp(expected);
        assertThat(entity.getTimestamp(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void setNullAndGetTimestamp() {
        final StepCountEntity entity = new StepCountEntity();
        entity.setTimestamp(null);
    }

    @Test
    public void getDefaultStepCount() {
        final StepCountEntity entity = new StepCountEntity();
        assertThat(entity.getStepCount(), is(0));
    }

    @Test
    public void setAndGetStepCount() {
        final StepCountEntity entity = new StepCountEntity();
        final int expected = 10;
        entity.setStepCount(expected);
        assertThat(entity.getStepCount(), is(expected));
    }

}