package com.edu.uni.augsburg.uniatron.domain.model;

import com.edu.uni.augsburg.uniatron.model.TimeCreditType;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TimeCreditEntityTest {

    @Test
    public void getDefaultId() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        assertThat(entity.getId(), is(0L));
    }

    @Test
    public void setAndGetId() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        final long expected = 10L;
        entity.setId(expected);
        assertThat(entity.getId(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void getDefaultTimestamp() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        assertThat(entity.getTimestamp(), is(nullValue()));
    }

    @Test
    public void setAndGetTimestamp() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        final Date expected = new Date();
        entity.setTimestamp(expected);
        assertThat(entity.getTimestamp(), is(equalTo(expected)));
    }

    @Test
    public void getDefaultTime() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        assertThat(entity.getTimeBonus(), is(0));
    }

    @Test
    public void setAndGetTime() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        final int expected = 10;
        entity.setTime(expected);
        assertThat(entity.getTimeBonus(), is(equalTo(expected)));
    }

    @Test
    public void getDefaultStepCount() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        assertThat(entity.getStepCount(), is(0));
    }

    @Test
    public void setAndGetStepCount() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        final int expected = 10;
        entity.setStepCount(expected);
        assertThat(entity.getStepCount(), is(expected));
    }

    @Test
    public void getDefaultType() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        assertThat(entity.getType(), is(nullValue()));
    }

    @Test
    public void setAndGetType() {
        final TimeCreditEntity entity = new TimeCreditEntity();
        final TimeCreditType expected = TimeCreditType.MINUTES_FOR_STEPS;
        entity.setType(expected);
        assertThat(entity.getType(), is(expected));
    }
}