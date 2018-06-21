package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMaxTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMinTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getDate;
import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TimeCreditDaoTest {
    private AppDatabase mDb;
    private TimeCreditDao mDao;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDao = mDb.timeCreditDao();
    }

    @After
    public void tearDown() {
        mDb.close();
    }

    @Test
    public void add() {
        final TimeCreditEntity timeCreditEntity1 = createTestData(1);
        mDao.add(timeCreditEntity1);

        assertThat(timeCreditEntity1.getId(), is(notNullValue()));
    }

    @Test
    public void loadTimeCredits() throws Exception {
        mDao.add(createTestData(1));
        mDao.add(createTestData(1));
        mDao.add(createTestData(2));
        mDao.add(createTestData(3));
        mDao.add(createTestData(2));

        final Date date = getDate(1, 1, 2018);
        final LiveData<Integer> data = mDao
                .loadTimeCredits(extractMinTimeOfDate(date), extractMaxTimeOfDate(date));

        assertThat(getLiveDataValue(data), is(10));
    }

    @Test
    public void getLatestLearningAidActiveTrue() throws Exception {
        final TimeCreditEntity testData = new TimeCreditEntity();
        testData.setType(TimeCredits.CREDIT_LEARNING.getType());
        testData.setTime(TimeCredits.CREDIT_LEARNING.getTime());
        testData.setStepCount(TimeCredits.CREDIT_LEARNING.getStepCount());
        testData.setTimestamp(new Date());
        mDao.add(testData);

        final LiveData<Long> learningAidActive = mDao.getLatestLearningAid(
                TimeCredits.CREDIT_LEARNING.getBlockedMinutes()
        );

        final Long liveDataValue = getLiveDataValue(learningAidActive);
        assertThat(liveDataValue > 0, is(Boolean.TRUE));
    }

    @Test
    public void getLatestLearningAidActiveFalse() throws Exception {
        final TimeCreditEntity testData = new TimeCreditEntity();
        testData.setType(TimeCredits.CREDIT_LEARNING.getType());
        testData.setTime(TimeCredits.CREDIT_LEARNING.getTime());
        testData.setStepCount(TimeCredits.CREDIT_LEARNING.getStepCount());
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.MINUTE, -(TimeCredits.CREDIT_LEARNING.getBlockedMinutes() + 1));
        testData.setTimestamp(calendar.getTime());
        mDao.add(testData);

        final LiveData<Long> learningAidActive = mDao.getLatestLearningAid(
                TimeCredits.CREDIT_LEARNING.getBlockedMinutes()
        );

        assertThat(getLiveDataValue(learningAidActive) > 0, is(Boolean.FALSE));
    }

    private TimeCreditEntity createTestData(int month) {
        final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
        timeCreditEntity.setTime(5);
        timeCreditEntity.setTimestamp(getDate(1, month, 2018));
        return timeCreditEntity;
    }
}