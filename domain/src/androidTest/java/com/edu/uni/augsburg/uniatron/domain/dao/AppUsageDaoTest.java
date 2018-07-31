package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.dao.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.domain.query.AppUsageQuery;
import com.edu.uni.augsburg.uniatron.domain.table.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.table.TimeCreditEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getDate;
import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AppUsageDaoTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private AppDatabase mDb;
    private AppUsageQuery mDao;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDao = mDb.appUsageQuery();
    }

    @After
    public void tearDown() {
        mDb.close();
    }

    @Test
    public void add() {
        final AppUsageEntity appUsageEntity = create("Test", new Date());
        mDao.add(appUsageEntity);

        assertThat(appUsageEntity.getId(), is(notNullValue()));
    }

    @Test
    public void loadAppUsageTime() throws Exception {
        final String appName0 = "Test";
        final Date date = getDate(1, 1, 2018);
        mDao.add(create(appName0, date));
        mDao.add(create(appName0, date));
        mDao.add(create(appName0, date));

        final String appName1 = "Test1";
        mDao.add(create(appName1, date));

        final String appName2 = "Test2";
        mDao.add(create(appName2, date));
        mDao.add(create(appName2, date));

        final LiveData<List<AppUsageEntity>> data = mDao
                .loadAppUsageTime(DateConverter.getMin(Calendar.DATE).convert(date), DateConverter.getMax(Calendar.DATE).convert(date));

        final List<AppUsageEntity> liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.isEmpty(), is(false));
        assertThat(liveDataValue.get(0).getPackageName(), is(equalTo(appName0)));
        assertThat(liveDataValue.get(1).getPackageName(), is(equalTo(appName2)));
        assertThat(liveDataValue.get(2).getPackageName(), is(equalTo(appName1)));
        assertThat(liveDataValue.get(0).getUsageTime(), is(30L));
        assertThat(liveDataValue.get(1).getUsageTime(), is(20L));
        assertThat(liveDataValue.get(2).getUsageTime(), is(10L));
    }

    @Test
    public void loadRemainingAppUsageTime() throws InterruptedException {
        final Date date = new Date();

        final AppUsageEntity test = create("app1", date);
        mDao.add(test);
        mDao.add(create("app2", date));
        mDao.add(create("app3", date));
        mDao.add(create("app4", date));
        mDao.add(create("app5", date));
        mDao.add(create("app6", date));

        final TimeCredits credits = TimeCredits.CREDIT_1000;
        final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
        timeCreditEntity.setTimeBonus(credits.getTimeBonus());
        timeCreditEntity.setStepCount(credits.getStepCount());
        timeCreditEntity.setTimestamp(date);
        mDb.timeCreditQuery().add(timeCreditEntity);

        final Set<String> filters = new HashSet<>(Collections.singletonList("app1"));

        final LiveData<Long> liveData = mDao.loadRemainingAppUsageTimeByBlacklist(
                DateConverter.getMin(Calendar.DATE).convert(date),
                DateConverter.getMax(Calendar.DATE).convert(date),
                filters
        );

        final Long liveDataValue = getLiveDataValue(liveData);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(credits.getTimeBonus() - test.getUsageTime()));
    }

    private AppUsageEntity create(String name, Date date) {
        final AppUsageEntity appUsageEntity = new AppUsageEntity();
        appUsageEntity.setPackageName(name);
        appUsageEntity.setTimestamp(date);
        appUsageEntity.setUsageTime(10);
        return appUsageEntity;
    }
}