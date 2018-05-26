package com.edu.uni.augsburg.uniatron.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmotionsTest {

    @Test
    public void getAveragePositiveRoundUp() {
        final Emotions emotions = Emotions.getAverage(1.6d);
        assertThat(emotions, is(Emotions.NEUTRAL));
    }

    @Test
    public void getAverageNegative() {
        final Emotions emotions = Emotions.getAverage(-1d);
        assertThat(emotions, is(Emotions.NEUTRAL));
    }

    @Test
    public void getAveragePositiveRoundDown() {
        final Emotions emotions = Emotions.getAverage(1.3d);
        assertThat(emotions, is(Emotions.SADNESS));
    }
}