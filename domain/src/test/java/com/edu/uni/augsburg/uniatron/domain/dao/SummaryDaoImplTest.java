package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.edu.uni.augsburg.uniatron.domain.dao.model.Summary;
import com.edu.uni.augsburg.uniatron.domain.query.SummaryQuery;
import com.edu.uni.augsburg.uniatron.domain.table.SummaryEntity;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SummaryDaoImplTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private SummaryQuery mSummaryQuery;
    private SummaryDaoImpl mSummaryDao;

    @Before
    public void setUp() {
        mSummaryQuery = mock(SummaryQuery.class);
        mSummaryDao = new SummaryDaoImpl(mSummaryQuery);
    }

    @Test
    public void getSummaryNull() throws InterruptedException {
        final MutableLiveData<List<SummaryEntity>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(null);
        when(mSummaryQuery.getSummariesByDate(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mSummaryDao.getSummaryByDate(date, date);

        final List<Summary> liveDataValue = TestUtils.getLiveDataValue(summary);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.isEmpty(), is(true));
    }

    @Test
    public void getSummaryByDate() throws InterruptedException {
        final List<SummaryEntity> summaryEntities = new ArrayList<>();
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());

        final MutableLiveData<List<SummaryEntity>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(summaryEntities);

        when(mSummaryQuery.getSummariesByDate(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mSummaryDao.getSummaryByDate(date, date);

        final List<Summary> liveDataValue = TestUtils.getLiveDataValue(summary);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(summaryEntities.size()));
    }

    @Test
    public void getSummaryByMonth() throws InterruptedException {
        final List<SummaryEntity> summaryEntities = new ArrayList<>();
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());

        final MutableLiveData<List<SummaryEntity>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(summaryEntities);

        when(mSummaryQuery.getSummariesByMonth(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mSummaryDao.getSummaryByMonth(date, date);

        final List<Summary> liveDataValue = TestUtils.getLiveDataValue(summary);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(summaryEntities.size()));
    }

    @Test
    public void getSummaryByYear() throws InterruptedException {
        final List<SummaryEntity> summaryEntities = new ArrayList<>();
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());

        final MutableLiveData<List<SummaryEntity>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(summaryEntities);

        when(mSummaryQuery.getSummariesByYear(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mSummaryDao.getSummaryByYear(date, date);

        final List<Summary> liveDataValue = TestUtils.getLiveDataValue(summary);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(summaryEntities.size()));
    }
}