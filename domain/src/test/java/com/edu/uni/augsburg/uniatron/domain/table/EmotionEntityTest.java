package com.edu.uni.augsburg.uniatron.domain.table;

import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotions;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class EmotionEntityTest {

    @Test
    public void getDefaultId() {
        final EmotionEntity entity = new EmotionEntity();
        assertThat(entity.getId(), is(0L));
    }

    @Test
    public void setAndGetId() {
        final EmotionEntity entity = new EmotionEntity();
        final long expected = 10L;
        entity.setId(expected);
        assertThat(entity.getId(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void getDefaultTimestamp() {
        final EmotionEntity entity = new EmotionEntity();
        assertThat(entity.getTimestamp(), is(nullValue()));
    }

    @Test
    public void setAndGetTimestamp() {
        final EmotionEntity entity = new EmotionEntity();
        final Date expected = new Date();
        entity.setTimestamp(expected);
        assertThat(entity.getTimestamp(), is(equalTo(expected)));
    }

    @Test(expected = NullPointerException.class)
    public void setNullAndGetTimestamp() {
        final EmotionEntity entity = new EmotionEntity();
        entity.setTimestamp(null);
    }

    @Test
    public void getDefaultValue() {
        final EmotionEntity entity = new EmotionEntity();
        assertThat(entity.getValue(), is(nullValue()));
    }

    @Test
    public void setAndGetValue() {
        final EmotionEntity entity = new EmotionEntity();
        final Emotions expected = Emotions.NEUTRAL;
        entity.setValue(expected);
        assertThat(entity.getValue(), is(equalTo(expected)));
    }
}