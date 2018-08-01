package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.edu.uni.augsburg.uniatron.domain.dao.model.StepCount;
import com.edu.uni.augsburg.uniatron.domain.query.StepCountQuery;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StepCountDaoImplTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private StepCountQuery mStepCountQuery;
    private StepCountDaoImpl mStepCountDao;

    @Before
    public void setUp() {
        mStepCountQuery = mock(StepCountQuery.class);
        mStepCountDao = new StepCountDaoImpl(mStepCountQuery);
    }

    @Test
    public void addStepCount() throws InterruptedException {
        final int value = 10;
        final LiveData<StepCount> stepCount = mStepCountDao.addStepCount(value);

        final StepCount liveDataValue = TestUtils.getLiveDataValue(stepCount);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getStepCount(), is(value));
    }

    @Test
    public void getRemainingStepCounts() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(mStepCountQuery.loadRemainingStepCount(any(), any())).thenReturn(liveData);

        final LiveData<Integer> data = mStepCountDao.getRemainingStepCountsToday();

        final Integer liveDataValue = TestUtils.getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
    }

    @Test
    public void getRemainingStepCountsNegative() throws InterruptedException {
        final int value = -10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(mStepCountQuery.loadRemainingStepCount(any(), any())).thenReturn(liveData);

        final LiveData<Integer> data = mStepCountDao.getRemainingStepCountsToday();

        final Integer liveDataValue = TestUtils.getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(0));
    }
}