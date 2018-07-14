package com.edu.uni.augsburg.uniatron.domain.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateConverterTest {

    @Test
    public void getMinTimeOfDate() {
        final Date now = new Date();

        final Date date = DateConverter.DATE_MIN_TIME.convert(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void getMaxTimeOfDate() {
        final Date now = new Date();

        final Date date = DateConverter.DATE_MAX_TIME.convert(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(calendar.get(Calendar.MINUTE), is(59));
        assertThat(calendar.get(Calendar.SECOND), is(59));
    }

    @Test
    public void getMinDateOfMonth() {
        final Date now = new Date();

        final Date date = DateConverter.MONTH_MIN_DATE.convert(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.DATE), is(1));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void getMaxDateOfMonth() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, 6, 1);

        final Date date = DateConverter.MONTH_MAX_DATE.convert(calendar.getTime());

        calendar.setTime(date);

        assertThat(calendar.get(Calendar.DATE), is(31));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(calendar.get(Calendar.MINUTE), is(59));
        assertThat(calendar.get(Calendar.SECOND), is(59));
    }

    @Test
    public void getMinMonthOfYear() {
        final Date now = new Date();

        final Date date = DateConverter.YEAR_MIN_MONTH.convert(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.MONTH), is(0));
        assertThat(calendar.get(Calendar.DATE), is(1));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void getMaxMonthOfYear() {
        final Date now = new Date();

        final Date date = DateConverter.YEAR_MAX_MONTH.convert(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.MONTH), is(Calendar.DECEMBER));
        assertThat(calendar.get(Calendar.DATE), is(31));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(calendar.get(Calendar.MINUTE), is(59));
        assertThat(calendar.get(Calendar.SECOND), is(59));
    }

    @Test
    public void getConverters() {
        assertThat(DateConverter.getDateConverterMin(Calendar.DATE), is(DateConverter.DATE_MIN_TIME));
        assertThat(DateConverter.getDateConverterMax(Calendar.DATE), is(DateConverter.DATE_MAX_TIME));
        assertThat(DateConverter.getDateConverterMin(Calendar.MONTH), is(DateConverter.MONTH_MIN_DATE));
        assertThat(DateConverter.getDateConverterMax(Calendar.MONTH), is(DateConverter.MONTH_MAX_DATE));
        assertThat(DateConverter.getDateConverterMin(Calendar.YEAR), is(DateConverter.YEAR_MIN_MONTH));
        assertThat(DateConverter.getDateConverterMax(Calendar.YEAR), is(DateConverter.YEAR_MAX_MONTH));
    }
}