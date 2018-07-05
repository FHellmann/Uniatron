package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.getMaxTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.getMinTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getDate;
import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class StepCountDaoTest {
    private AppDatabase mDb;
    private StepCountDao mDao;
    private TimeCreditDao mDaoCredit;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDao = mDb.stepCountDao();
        mDaoCredit = mDb.timeCreditDao();
    }

    @After
    public void tearDown() {
        mDb.close();
    }

    @Test
    public void add() {
        final StepCountEntity stepCountEntity = create(1, new Date());
        mDao.add(stepCountEntity);

        assertThat(stepCountEntity.getId(), is(notNullValue()));
    }

    @Test
    public void loadStepCounts() throws Exception {
        final int count = 10;
        final Date date = getDate(1, 1, 2018);
        mDao.add(create(count, date));
        mDao.add(create(count, date));
        mDao.add(create(count, getDate(3, 1, 2018)));
        mDao.add(create(count, getDate(1, 2, 2018)));
        mDao.add(create(count, date));

        final LiveData<Integer> data = mDao
                .loadStepCounts(getMinTimeOfDate(date), getMaxTimeOfDate(date));

        final Integer liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(count * 3));
    }

    @Test
    public void loadRemainingStepCountsNegative() throws Exception {
        final int count = 10;
        final Date date = getDate(1, 1, 2018);

        TimeCreditEntity entry = new TimeCreditEntity();
        entry.setStepCount(count);
        entry.setTimestamp(date);
        entry.setTime(2);
        mDaoCredit.add(entry);

        final LiveData<Integer> data = mDao
                .loadRemainingStepCount(getMinTimeOfDate(date), getMaxTimeOfDate(date));

        final Integer liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(-count));
    }

    @Test
    public void loadRemainingStepCountsZero() throws Exception {
        final int count = 10;
        final Date date = getDate(1, 1, 2018);
        mDao.add(create(count, date));

        TimeCreditEntity entry = new TimeCreditEntity();
        entry.setStepCount(count);
        entry.setTimestamp(date);
        entry.setTime(2);
        mDaoCredit.add(entry);

        final LiveData<Integer> data = mDao
                .loadRemainingStepCount(getMinTimeOfDate(date), getMaxTimeOfDate(date));

        final Integer liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(0));
    }

    @Test
    public void loadRemainingStepCountsPositive() throws Exception {
        final int count = 10;
        final Date date = getDate(1, 1, 2018);
        mDao.add(create(count, date));

        final LiveData<Integer> data = mDao
                .loadRemainingStepCount(getMinTimeOfDate(date), getMaxTimeOfDate(date));

        final Integer liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(count));
    }

    private StepCountEntity create(int count, Date date) {
        final StepCountEntity stepCountEntity = new StepCountEntity();
        stepCountEntity.setStepCount(count);
        stepCountEntity.setTimestamp(date);
        return stepCountEntity;
    }
}