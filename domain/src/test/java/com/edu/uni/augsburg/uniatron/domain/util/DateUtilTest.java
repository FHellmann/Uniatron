package com.edu.uni.augsburg.uniatron.domain.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.equalTo;
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

    @Test
    public void extractMinDateOfMonth() {
        final Date now = new Date();

        final Date date = DateUtil.extractMinDateOfMonth(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.DATE), is(1));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void extractMinMonthOfYear() {
        final Date now = new Date();

        final Date date = DateUtil.extractMinMonthOfYear(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.MONTH), is(0));
        assertThat(calendar.get(Calendar.DATE), is(1));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void formatForDate() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, 6, 1);

        final String dateString = DateUtil.formatForDate(calendar.getTime());

        assertThat(dateString, is(equalTo("1. Jul 2018")));
    }

    @Test
    public void formatForMonth() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, 6, 1);

        final String dateString = DateUtil.formatForMonth(calendar.getTime());

        assertThat(dateString, is(equalTo("Juli 2018")));
    }

    @Test
    public void formatForYear() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, 6, 1);

        final String dateString = DateUtil.formatForYear(calendar.getTime());

        assertThat(dateString, is(equalTo("2018")));
    }
}