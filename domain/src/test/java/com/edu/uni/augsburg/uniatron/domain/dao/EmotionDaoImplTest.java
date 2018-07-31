package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotion;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotions;
import com.edu.uni.augsburg.uniatron.domain.query.EmotionQuery;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class EmotionDaoImplTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private EmotionQuery mEmotionQuery;
    private EmotionDaoImpl mEmotionDao;

    @Before
    public void setUp() {
        mEmotionQuery = mock(EmotionQuery.class);
        mEmotionDao = new EmotionDaoImpl(mEmotionQuery);
    }

    @Test
    public void addEmotion() throws InterruptedException {
        final Emotions value = Emotions.NEUTRAL;
        final LiveData<Emotion> emotion = mEmotionDao.addEmotion(value);

        final Emotion liveDataValue = TestUtils.getLiveDataValue(emotion);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
    }
}