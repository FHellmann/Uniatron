package com.edu.uni.augsburg.uniatron.domain;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.EmotionDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.SummaryDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.Emotion;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.LearningAid;
import com.edu.uni.augsburg.uniatron.model.StepCount;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataRepositoryTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private DataRepository mRepository;
    private AppUsageDao appUsageDao;
    private StepCountDao stepCountDao;
    private TimeCreditDao timeCreditDao;
    private SummaryDao summaryDao;
    private EmotionDao emotionDao;

    @Before
    public void setUp() {
        appUsageDao = mock(AppUsageDao.class);
        stepCountDao = mock(StepCountDao.class);
        timeCreditDao = mock(TimeCreditDao.class);
        emotionDao = mock(EmotionDao.class);
        summaryDao = mock(SummaryDao.class);

        final AppDatabase database = mock(AppDatabase.class);
        when(database.appUsageDao()).thenReturn(appUsageDao);
        when(database.stepCountDao()).thenReturn(stepCountDao);
        when(database.timeCreditDao()).thenReturn(timeCreditDao);
        when(database.emotionDao()).thenReturn(emotionDao);
        when(database.summaryDao()).thenReturn(summaryDao);

        mRepository = new DataRepository(database);
    }

    @Test
    public void addTimeCredit() throws InterruptedException {
        final TimeCredits timeCredits = TimeCredits.CREDIT_1000;
        final LiveData<TimeCredit> timeCredit = mRepository.addTimeCredit(timeCredits, 1.0);

        final TimeCredit liveDataValue = TestUtils.getLiveDataValue(timeCredit);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getTimeBonus(), is(timeCredits.getTimeBonus()));
        assertThat(liveDataValue.getStepCount(), is(timeCredits.getStepCount()));
    }

    @Test
    public void getLatestLearningAidNull() throws InterruptedException {
        final MutableLiveData<Date> liveData = new MutableLiveData<>();
        liveData.setValue(null);
        when(timeCreditDao.getLatestLearningAid()).thenReturn(liveData);

        final LiveData<LearningAid> latestLearningAidDiff = mRepository.getLatestLearningAid();

        final LearningAid liveDataValue = TestUtils.getLiveDataValue(latestLearningAidDiff);
        assertThat(liveDataValue.isActive(), is(false));
    }

    @Test
    public void getLatestLearningAidInactive() throws InterruptedException {
        final MutableLiveData<Date> liveData = new MutableLiveData<>();
        liveData.setValue(new Date(System.currentTimeMillis() - TimeCredits.CREDIT_LEARNING.getBlockedTime() * 60 * 1000 - 1));
        when(timeCreditDao.getLatestLearningAid()).thenReturn(liveData);

        final LiveData<LearningAid> latestLearningAidDiff = mRepository.getLatestLearningAid();

        final LearningAid liveDataValue = TestUtils.getLiveDataValue(latestLearningAidDiff);
        assertThat(liveDataValue.isActive(), is(false));
    }

    @Test
    public void getLatestLearningAidActive() throws InterruptedException {
        final MutableLiveData<Date> liveData = new MutableLiveData<>();
        liveData.setValue(new Date(System.currentTimeMillis() - 10000));
        when(timeCreditDao.getLatestLearningAid()).thenReturn(liveData);

        final LiveData<LearningAid> latestLearningAidDiff = mRepository.getLatestLearningAid();

        final LearningAid liveDataValue = TestUtils.getLiveDataValue(latestLearningAidDiff);
        assertThat(liveDataValue.isActive(), is(true));
        assertThat(liveDataValue.getLeftTime(), is(greaterThan(0L)));
    }

    @Test
    public void addStepCount() throws InterruptedException {
        final int value = 10;
        final LiveData<StepCount> stepCount = mRepository.addStepCount(value);

        final StepCount liveDataValue = TestUtils.getLiveDataValue(stepCount);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getStepCount(), is(value));
    }

    @Test
    public void getRemainingStepCounts() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(stepCountDao.loadRemainingStepCount(any(), any())).thenReturn(liveData);

        final LiveData<Integer> data = mRepository.getRemainingStepCountsToday();

        final Integer liveDataValue = TestUtils.getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
    }

    @Test
    public void getRemainingStepCountsNegative() throws InterruptedException {
        final int value = -10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(stepCountDao.loadRemainingStepCount(any(), any())).thenReturn(liveData);

        final LiveData<Integer> data = mRepository.getRemainingStepCountsToday();

        final Integer liveDataValue = TestUtils.getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(0));
    }

    @Test
    public void addAppUsage() throws InterruptedException {
        final long value = 10;
        final LiveData<AppUsage> appUsage = mRepository.addAppUsage("test", value);

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

        final LiveData<Date> minDate = mRepository.getMinDate();

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

        final LiveData<Long> data = mRepository.getRemainingAppUsageTimeToday(filters);

        final Long liveDataValue = TestUtils.getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
    }

    @Test
    public void addEmotion() throws InterruptedException {
        final Emotions value = Emotions.NEUTRAL;
        final LiveData<Emotion> emotion = mRepository.addEmotion(value);

        final Emotion liveDataValue = TestUtils.getLiveDataValue(emotion);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getValue(), is(value));
    }

    @Test
    public void getSummaryNull() throws InterruptedException {
        final MutableLiveData<List<SummaryEntity>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(null);
        when(summaryDao.getSummariesByDate(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mRepository.getSummaryByDate(date, date);

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

        when(summaryDao.getSummariesByDate(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mRepository.getSummaryByDate(date, date);

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

        when(summaryDao.getSummariesByMonth(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mRepository.getSummaryByMonth(date, date);

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

        when(summaryDao.getSummariesByYear(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mRepository.getSummaryByYear(date, date);

        final List<Summary> liveDataValue = TestUtils.getLiveDataValue(summary);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(summaryEntities.size()));
    }
}
