package com.edu.uni.augsburg.uniatron.domain.dao.converter;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateConverterImplTest {

    @Test
    public void getMinTimeOfDate() {
        final Date now = new Date();

        final Date date = DateConverterImpl.DATE_MIN_TIME.convert(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(0));
        assertThat(calendar.get(Calendar.MINUTE), is(0));
        assertThat(calendar.get(Calendar.SECOND), is(0));
    }

    @Test
    public void getMaxTimeOfDate() {
        final Date now = new Date();

        final Date date = DateConverterImpl.DATE_MAX_TIME.convert(now);

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(calendar.get(Calendar.MINUTE), is(59));
        assertThat(calendar.get(Calendar.SECOND), is(59));
    }

    @Test
    public void getMinDateOfMonth() {
        final Date now = new Date();

        final Date date = DateConverterImpl.MONTH_MIN_DATE.convert(now);

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

        final Date date = DateConverterImpl.MONTH_MAX_DATE.convert(calendar.getTime());

        calendar.setTime(date);

        assertThat(calendar.get(Calendar.DATE), is(31));
        assertThat(calendar.get(Calendar.HOUR_OF_DAY), is(23));
        assertThat(calendar.get(Calendar.MINUTE), is(59));
        assertThat(calendar.get(Calendar.SECOND), is(59));
    }

    @Test
    public void getMinMonthOfYear() {
        final Date now = new Date();

        final Date date = DateConverterImpl.YEAR_MIN_MONTH.convert(now);

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

        final Date date = DateConverterImpl.YEAR_MAX_MONTH.convert(now);

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
        assertThat(DateConverter.getMin(Calendar.DATE), is(DateConverterImpl.DATE_MIN_TIME));
        assertThat(DateConverter.getMax(Calendar.DATE), is(DateConverterImpl.DATE_MAX_TIME));
        assertThat(DateConverter.getMin(Calendar.MONTH), is(DateConverterImpl.MONTH_MIN_DATE));
        assertThat(DateConverter.getMax(Calendar.MONTH), is(DateConverterImpl.MONTH_MAX_DATE));
        assertThat(DateConverter.getMin(Calendar.YEAR), is(DateConverterImpl.YEAR_MIN_MONTH));
        assertThat(DateConverter.getMax(Calendar.YEAR), is(DateConverterImpl.YEAR_MAX_MONTH));
    }
}