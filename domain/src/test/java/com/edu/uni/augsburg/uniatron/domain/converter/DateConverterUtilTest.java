package com.edu.uni.augsburg.uniatron.domain.converter;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class DateConverterUtilTest {

    @Test
    public void fromTimestamp() {
        final Date date = new Date();

        final Date result = DateConverterUtil.fromTimestamp(date.getTime());

        assertThat(result, is(equalTo(date)));
    }

    @Test
    public void fromTimestampNull() {
        assertThat(DateConverterUtil.fromTimestamp(null), is(nullValue()));
    }

    @Test
    public void dateToTimestamp() {
        final Date date = new Date();

        final Date result = new Date(DateConverterUtil.dateToTimestamp(date));

        assertThat(result, is(equalTo(date)));
    }

    @Test
    public void dateToTimestampNull() {
        assertThat(DateConverterUtil.dateToTimestamp(null), is(nullValue()));
    }
}