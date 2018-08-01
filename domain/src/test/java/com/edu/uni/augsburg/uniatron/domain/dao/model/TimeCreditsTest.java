package com.edu.uni.augsburg.uniatron.domain.dao.model;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TimeCreditsTest {

    @NonNull
    private final TimeCredits mTimeCredits;

    @Parameterized.Parameters
    public static TimeCredits[] getValues() {
        return TimeCredits.values();
    }

    public TimeCreditsTest(@NonNull final TimeCredits timeCredits) {
        mTimeCredits = timeCredits;
    }

    @Test
    public void getSteps() {
        assertThat(mTimeCredits.getStepCount() >= 0, is(true));
    }

    @Test
    public void getTime() {
        assertThat(mTimeCredits.getTimeBonus() > 0, is(true));
    }

    @Test
    public void getType() {
        assertThat(mTimeCredits.getType(), is(notNullValue()));
    }

    @Test
    public void getBlockedMinutes() {
        assertThat(mTimeCredits.getBlockedTime() >= 0, is(true));
    }
}