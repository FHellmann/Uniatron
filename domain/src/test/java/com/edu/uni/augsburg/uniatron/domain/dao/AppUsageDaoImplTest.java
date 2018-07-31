package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.edu.uni.augsburg.uniatron.domain.dao.model.AppUsage;
import com.edu.uni.augsburg.uniatron.domain.query.AppUsageQuery;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppUsageDaoImplTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private AppUsageQuery appUsageDao;
    private AppUsageDaoImpl mAppUsageDao;

    @Before
    public void setUp() {
        appUsageDao = mock(AppUsageQuery.class);
        mAppUsageDao = new AppUsageDaoImpl(appUsageDao);
    }

    @Test
    public void addAppUsage() throws InterruptedException {
        final long value = 10;
        final LiveData<AppUsage> appUsage = mAppUsageDao.addAppUsage("test", value);

        final AppUsage liveDataValue = TestUtils.getLiveDataValue(appUsage);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getUsageTime(), is(value));
    }

    @Test
    public void getMinDate() throws InterruptedException {
        final MutableLiveData<Date> liveData = new MutableLiveData<>();
        final Date expected = new Date();
        liveData.setValue(expected);
        when(appUsageDao.getMinDate(any(), any())).thenReturn(liveData);

        final LiveData<Date> minDate = mAppUsageDao.getMinDate();

        final Date liveDataValue = TestUtils.getLiveDataValue(minDate);
        assertThat(liveDataValue, is(equalTo(expected)));
    }

    @Test
    public void getRemainingAppUsageTime() throws InterruptedException {
        final long value = 10;
        final Set<String> filters = new HashSet<>(Collections.singletonList("app1"));

        final MutableLiveData<Long> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(appUsageDao.loadRemainingAppUsageTimeByBlacklist(any(), any(), any()))
                .thenReturn(liveData);

        final LiveData<Long> data = mAppUsageDao.getRemainingAppUsageTimeToday(filters);

        final Long liveDataValue = TestUtils.getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
    }
}