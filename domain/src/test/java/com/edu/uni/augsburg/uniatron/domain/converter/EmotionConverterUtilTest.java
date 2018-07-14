package com.edu.uni.augsburg.uniatron.domain.converter;

import com.edu.uni.augsburg.uniatron.model.Emotions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class EmotionConverterUtilTest {

    @Test
    public void fromRawValue() {
        final Emotions happiness = Emotions.HAPPINESS;

        final Emotions emotions = EmotionConverterUtil.fromRawValue(happiness.getIndex());

        assertThat(emotions, is(happiness));
    }

    @Test
    public void fromRawValueNull() {
        assertThat(EmotionConverterUtil.fromRawValue(null), is(nullValue()));
    }

    @Test
    public void fromRealValue() {
        final Emotions happiness = Emotions.HAPPINESS;

        final Integer emotionsIndex = EmotionConverterUtil.fromRealValue(happiness);

        assertThat(emotionsIndex, is(happiness.ordinal()));
    }

    @Test
    public void fromRealValueEmpty() {
        assertThat(EmotionConverterUtil.fromRealValue(null), is(nullValue()));
    }
}