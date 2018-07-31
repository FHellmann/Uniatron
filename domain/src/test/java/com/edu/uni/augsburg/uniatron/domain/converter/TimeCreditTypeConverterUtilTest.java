package com.edu.uni.augsburg.uniatron.domain.converter;

import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCreditType;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TimeCreditTypeConverterUtilTest {

    @Test
    public void fromRawValue() {
        final TimeCreditType learningAid = TimeCreditType.LEARNING_AID;
        final String rawValue = learningAid.name();

        final TimeCreditType timeCreditType = TimeCreditTypeConverterUtil.fromRawValue(rawValue);

        assertThat(timeCreditType, is(learningAid));
    }

    @Test
    public void fromRawValueNull() {
        assertThat(TimeCreditTypeConverterUtil.fromRawValue(null), is(nullValue()));
    }

    @Test
    public void fromRealValue() {
        final TimeCreditType learningAid = TimeCreditType.LEARNING_AID;

        final String timeCreditType = TimeCreditTypeConverterUtil.fromRealValue(learningAid);

        assertThat(TimeCreditType.valueOf(timeCreditType), is(learningAid));
    }

    @Test
    public void fromRealValueNull() {
        assertThat(TimeCreditTypeConverterUtil.fromRealValue(null), is(nullValue()));
    }
}