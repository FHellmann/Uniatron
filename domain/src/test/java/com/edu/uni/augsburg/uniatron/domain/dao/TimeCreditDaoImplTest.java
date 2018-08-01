package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.edu.uni.augsburg.uniatron.domain.dao.model.LearningAid;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.domain.query.TimeCreditQuery;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeCreditDaoImplTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private TimeCreditQuery mTimeCreditQuery;
    private TimeCreditDaoImpl mTimeCreditDao;


    @Before
    public void setUp() {
        mTimeCreditQuery = mock(TimeCreditQuery.class);
        mTimeCreditDao = new TimeCreditDaoImpl(mTimeCreditQuery);
    }

    @Test
    public void addTimeCredit() throws InterruptedException {
        final TimeCredits timeCredits = TimeCredits.CREDIT_1000;
        final LiveData<TimeCredit> timeCredit = mTimeCreditDao.addTimeCredit(timeCredits, 1.0);

        final TimeCredit liveDataValue = TestUtils.getLiveDataValue(timeCredit);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getTimeBonus(), is(timeCredits.getTimeBonus()));
        assertThat(liveDataValue.getStepCount(), is(timeCredits.getStepCount()));
    }

    @Test
    public void getLatestLearningAidNull() throws InterruptedException {
        final MutableLiveData<Date> liveData = new MutableLiveData<>();
        liveData.setValue(null);
        when(mTimeCreditQuery.getLatestLearningAid()).thenReturn(liveData);

        final LiveData<LearningAid> latestLearningAidDiff = mTimeCreditDao.getLatestLearningAid();

        final LearningAid liveDataValue = TestUtils.getLiveDataValue(latestLearningAidDiff);
        assertThat(liveDataValue.isActive(), is(false));
    }

    @Test
    public void getLatestLearningAidInactive() throws InterruptedException {
        final MutableLiveData<Date> liveData = new MutableLiveData<>();
        liveData.setValue(new Date(System.currentTimeMillis() - TimeCredits.CREDIT_LEARNING.getBlockedTime() * 60 * 1000 - 1));
        when(mTimeCreditQuery.getLatestLearningAid()).thenReturn(liveData);

        final LiveData<LearningAid> latestLearningAidDiff = mTimeCreditDao.getLatestLearningAid();

        final LearningAid liveDataValue = TestUtils.getLiveDataValue(latestLearningAidDiff);
        assertThat(liveDataValue.isActive(), is(false));
    }

    @Test
    public void getLatestLearningAidActive() throws InterruptedException {
        final MutableLiveData<Date> liveData = new MutableLiveData<>();
        liveData.setValue(new Date(System.currentTimeMillis() - 10000));
        when(mTimeCreditQuery.getLatestLearningAid()).thenReturn(liveData);

        final LiveData<LearningAid> latestLearningAidDiff = mTimeCreditDao.getLatestLearningAid();

        final LearningAid liveDataValue = TestUtils.getLiveDataValue(latestLearningAidDiff);
        assertThat(liveDataValue.isActive(), is(true));
        assertThat(liveDataValue.getLeftTime().get(), is(greaterThan(0L)));
    }
}