package com.edu.uni.augsburg.uniatron.domain.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateUtilTest {

    @Test
    public void extractMinTimeOfDate() {
        final Date now = new Date();

        final Date date = DateUtil.extractMinTimeOfDate(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void extractMaxTimeOfDate() {
        final Date now = new Date();

        final Date date = DateUtil.extractMaxTimeOfDate(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(calendar.get(Calendar.MINUTE), is(59));
        assertThat(calendar.get(Calendar.SECOND), is(59));
    }
}