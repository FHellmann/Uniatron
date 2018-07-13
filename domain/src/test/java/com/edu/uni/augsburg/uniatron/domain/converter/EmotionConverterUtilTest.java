package com.edu.uni.augsburg.uniatron.domain.converter;

import com.edu.uni.augsburg.uniatron.model.Emotions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmotionConverterUtilTest {

    @Test
    public void fromRawValue() {
        final Emotions happiness = Emotions.HAPPINESS;

        final Emotions emotions = EmotionConverterUtil.fromRawValue(happiness.ordinal());

        assertThat(emotions, is(happiness));
    }

    @Test
    public void fromRealValue() {
        final Emotions happiness = Emotions.HAPPINESS;

        final Integer emotionsOrdinal = EmotionConverterUtil.fromRealValue(happiness);

        assertThat(emotionsOrdinal, is(happiness.ordinal()));
    }
}