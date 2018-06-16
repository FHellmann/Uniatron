package com.edu.uni.augsburg.uniatron.domain.util;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateUtilTest {

    @Test
    public void extractMinTimeOfDate() {
        final Date now = new Date();

        final Date date = DateUtil.extractMinTimeOfDate(now);

        assertThat(date.getHours(), is(0));
        assertThat(date.getMinutes(), is(0));
        assertThat(date.getSeconds(), is(0));
    }

    @Test
    public void extractMaxTimeOfDate() {
        final Date now = new Date();

        final Date date = DateUtil.extractMaxTimeOfDate(now);

        assertThat(date.getHours(), is(23));
        assertThat(date.getMinutes(), is(59));
        assertThat(date.getSeconds(), is(59));
    }
}