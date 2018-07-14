package com.edu.uni.augsburg.uniatron.domain.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

public class DateFormatterTest {
    @Test
    public void formatForDate() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, 6, 1);

        final String dateString = DateFormatter.DD_MM_YYYY.format(calendar.getTime());

        assertThat(dateString, is(equalTo("1. Jul 2018")));
    }

    @Test
    public void formatForMonth() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, 6, 1);

        final String dateString = DateFormatter.MM_YYYY.format(calendar.getTime());

        assertThat(dateString, is(startsWith("Jul")));
        assertThat(dateString, is(endsWith("2018")));
    }

    @Test
    public void formatForYear() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2018, 6, 1);

        final String dateString = DateFormatter.YYYY.format(calendar.getTime());

        assertThat(dateString, is(equalTo("2018")));
    }
}