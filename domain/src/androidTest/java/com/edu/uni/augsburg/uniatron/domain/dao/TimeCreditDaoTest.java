package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

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

    private TimeCreditEntity createTestData(int month) {
        final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
        timeCreditEntity.setTime(5);
        timeCreditEntity.setTimestamp(getDate(1, month, 2018));
        return timeCreditEntity;
    }
}