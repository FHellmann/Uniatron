package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.domain.query.TimeCreditQuery;
import com.edu.uni.augsburg.uniatron.domain.table.TimeCreditEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getDate;
import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TimeCreditDaoTest {
    private AppDatabase mDb;
    private TimeCreditQuery mDao;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDao = mDb.timeCreditQuery();
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
    public void getLatestLearningAidActive() throws Exception {
        final TimeCreditEntity testData = new TimeCreditEntity();
        testData.setType(TimeCredits.CREDIT_LEARNING.getType());
        testData.setTimeBonus(TimeCredits.CREDIT_LEARNING.getTimeBonus());
        testData.setStepCount(TimeCredits.CREDIT_LEARNING.getStepCount());
        testData.setTimestamp(new Date());
        mDao.add(testData);
        final TimeCreditEntity testData2 = new TimeCreditEntity();
        testData2.setType(TimeCredits.CREDIT_LEARNING.getType());
        testData2.setTimeBonus(TimeCredits.CREDIT_LEARNING.getTimeBonus());
        testData2.setStepCount(TimeCredits.CREDIT_LEARNING.getStepCount());
        testData2.setTimestamp(new Date());
        mDao.add(testData2);

        final LiveData<Date> learningAidActive = mDao.getLatestLearningAid();

        final Date liveDataValue = getLiveDataValue(learningAidActive);
        assertThat(liveDataValue, is(equalTo(testData2.getTimestamp())));
    }

    private TimeCreditEntity createTestData(int month) {
        final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
        timeCreditEntity.setTimeBonus(5);
        timeCreditEntity.setTimestamp(getDate(1, month, 2018));
        return timeCreditEntity;
    }
}