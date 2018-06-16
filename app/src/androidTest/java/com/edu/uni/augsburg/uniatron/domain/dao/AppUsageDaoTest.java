package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMaxTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMinTimeOfDate;
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
    private AppUsageDao mDao;

    @Before
    public void setUp() {
        final Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        mDao = mDb.appUsageDao();
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
                .loadAppUsageTime(extractMinTimeOfDate(date), extractMaxTimeOfDate(date));

        final List<AppUsageEntity> liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.isEmpty(), is(false));
        assertThat(liveDataValue.get(0).getAppName(), is(equalTo(appName0)));
        assertThat(liveDataValue.get(1).getAppName(), is(equalTo(appName2)));
        assertThat(liveDataValue.get(2).getAppName(), is(equalTo(appName1)));
        assertThat(liveDataValue.get(0).getTime(), is(30));
        assertThat(liveDataValue.get(1).getTime(), is(20));
        assertThat(liveDataValue.get(2).getTime(), is(10));
    }

    @Test
    public void loadAppUsagePercent() throws Exception {
        final String appName0 = "Test";
        final Date date = getDate(1, 1, 2018);
        mDao.add(create(appName0, date));
        mDao.add(create(appName0, date));
        mDao.add(create(appName0, date));

        final String appName1 = "Test1";
        mDao.add(create(appName1, date));

        final LiveData<List<AppUsageEntity>> data = mDao
                .loadAppUsagePercent(extractMinTimeOfDate(date), extractMaxTimeOfDate(date));

        final List<AppUsageEntity> liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.isEmpty(), is(false));
        assertThat(liveDataValue.get(0).getAppName(), is(equalTo(appName0)));
        assertThat(liveDataValue.get(1).getAppName(), is(equalTo(appName1)));
        assertThat(liveDataValue.get(0).getTime(), is(75));
        assertThat(liveDataValue.get(1).getTime(), is(25));
    }

    @Test
    public void loadRemainingAppUsageTime() throws InterruptedException {
        final Date date = new Date();

        final AppUsageEntity test = create("Test", date);
        mDao.add(test);

        final TimeCredits credits = TimeCredits.CREDIT_1000;
        final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
        timeCreditEntity.setTime(credits.getTimeInMinutes());
        timeCreditEntity.setStepCount(credits.getStepCount());
        timeCreditEntity.setTimestamp(date);
        mDb.timeCreditDao().add(timeCreditEntity);

        final LiveData<Integer> liveData = mDao
                .loadRemainingAppUsageTime(extractMinTimeOfDate(date), extractMaxTimeOfDate(date));

        final Integer liveDataValue = getLiveDataValue(liveData);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(credits.getTimeInMinutes() * 60 - test.getTime()));
    }

    private AppUsageEntity create(String name, Date date) {
        final AppUsageEntity appUsageEntity = new AppUsageEntity();
        appUsageEntity.setAppName(name);
        appUsageEntity.setTimestamp(date);
        appUsageEntity.setTime(10);
        return appUsageEntity;
    }
}