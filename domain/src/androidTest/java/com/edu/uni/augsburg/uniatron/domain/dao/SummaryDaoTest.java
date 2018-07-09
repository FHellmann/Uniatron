package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.util.DatabaseUtil;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SummaryDaoTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private AppDatabase mDb;
    private SummaryDao mDao;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDao = mDb.summaryDao();
    }

    @After
    public void tearDown() {
        mDb.close();
    }

    @Test
    public void getSummariesEmpty() throws InterruptedException {
        final Date date = new Date();

        final LiveData<List<SummaryEntity>> summaries = mDao.getSummariesByDate(date, date);

        final List<SummaryEntity> liveDataValue = getLiveDataValue(summaries);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.isEmpty(), is(true));
    }

    @Test
    public void getSummaries() throws InterruptedException {
        DatabaseUtil.createRandomData(mDb);

        final Date date = new Date();

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DATE, -7);

        final LiveData<List<SummaryEntity>> summaries = mDao.getSummariesByDate(calendar.getTime(), date);

        final List<SummaryEntity> liveDataValue = getLiveDataValue(summaries);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.isEmpty(), is(false));
    }

    @Test
    public void getSummariesUpdate() throws InterruptedException {
        final Date date = new Date();

        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DATE, -7);

        final LiveData<List<SummaryEntity>> summaries = mDao.getSummariesByDate(calendar.getTime(), date);

        final List<SummaryEntity> liveDataValue = getLiveDataValue(summaries);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.isEmpty(), is(true));

        DatabaseUtil.createRandomData(mDb);

        final List<SummaryEntity> liveDataValue1 = getLiveDataValue(summaries);
        assertThat(liveDataValue1, is(notNullValue()));
        assertThat(liveDataValue1.isEmpty(), is(false));
    }
}