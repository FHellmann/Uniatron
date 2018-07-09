package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;
import com.edu.uni.augsburg.uniatron.model.Emotions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.getMaxTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.getMinTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class EmotionDaoTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private AppDatabase mDb;
    private EmotionDao mDao;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDao = mDb.emotionDao();
    }

    @After
    public void tearDown() {
        mDb.close();
    }

    @Test
    public void insert() {
        final EmotionEntity emotionEntity = new EmotionEntity();
        emotionEntity.setValue(Emotions.NEUTRAL);
        emotionEntity.setTimestamp(new Date());
        mDao.add(emotionEntity);

        assertThat(emotionEntity.getId(), is(notNullValue()));
    }

    @Test
    public void getAll() throws InterruptedException {
        final EmotionEntity emotionEntity = new EmotionEntity();
        emotionEntity.setValue(Emotions.NEUTRAL);
        emotionEntity.setTimestamp(new Date());
        mDao.add(emotionEntity);

        final EmotionEntity emotionEntity1 = new EmotionEntity();
        emotionEntity1.setValue(Emotions.HAPPINESS);
        emotionEntity1.setTimestamp(new Date());
        mDao.add(emotionEntity1);

        final Date date = new Date();
        final LiveData<List<EmotionEntity>> liveData = mDao
                .getAll(getMinTimeOfDate(date), getMaxTimeOfDate(date));

        final List<EmotionEntity> liveDataValue = getLiveDataValue(liveData);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(2));
    }

    @Test
    public void getAverageEmotion() throws InterruptedException {
        final EmotionEntity emotionEntity = new EmotionEntity();
        emotionEntity.setValue(Emotions.NEUTRAL);
        emotionEntity.setTimestamp(new Date());
        mDao.add(emotionEntity);

        final EmotionEntity emotionEntity1 = new EmotionEntity();
        emotionEntity1.setValue(Emotions.HAPPINESS);
        emotionEntity1.setTimestamp(new Date());
        mDao.add(emotionEntity1);

        final Date date = new Date();
        final LiveData<Double> liveData = mDao
                .getAverageEmotion(getMinTimeOfDate(date), getMaxTimeOfDate(date));

        final double expected = (Emotions.NEUTRAL.ordinal() + Emotions.HAPPINESS.ordinal()) / 2.0;

        final Double liveDataValue = getLiveDataValue(liveData);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(expected));
    }
}