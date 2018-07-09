package com.edu.uni.augsburg.uniatron.domain.converter;

import com.edu.uni.augsburg.uniatron.model.TimeCreditType;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TimeCreditTypeConverterUtilTest {

    @Test
    public void fromRawValue() {
        final TimeCreditType learningAid = TimeCreditType.LEARNING_AID;
        final String rawValue = learningAid.name();

        final TimeCreditType timeCreditType = TimeCreditTypeConverterUtil.fromRawValue(rawValue);

        assertThat(timeCreditType, is(learningAid));
    }

    @Test
    public void fromRealValue() {
        final TimeCreditType learningAid = TimeCreditType.LEARNING_AID;

        final String timeCreditType = TimeCreditTypeConverterUtil.fromRealValue(learningAid);

        assertThat(TimeCreditType.valueOf(timeCreditType), is(learningAid));
    }
}