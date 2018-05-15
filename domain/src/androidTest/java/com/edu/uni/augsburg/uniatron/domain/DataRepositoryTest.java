package com.edu.uni.augsburg.uniatron.domain;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.test.InstrumentationRegistry;

import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.EmotionDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.SummaryDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.Emotion;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.StepCount;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.edu.uni.augsburg.uniatron.domain.util.TestUtils.getLiveDataValue;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        final LiveData<TimeCredit> timeCredit = mRepository.addTimeCredit(timeCredits);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final TimeCredit liveDataValue = getLiveDataValue(timeCredit);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getTime(), is(timeCredits.getTimeInMinutes()));
        assertThat(liveDataValue.getStepCount(), is(timeCredits.getStepCount()));
    }

    @Test
    public void getTimeCredits() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(timeCreditDao.loadTimeCredits(any(), any())).thenReturn(liveData);

        final LiveData<Integer> timeCreditsToday = mRepository.getTimeCreditsToday();

        final Integer liveDataValue = getLiveDataValue(timeCreditsToday);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
        verify(timeCreditDao, atLeastOnce()).loadTimeCredits(any(), any());
    }

    @Test
    public void addStepCount() throws InterruptedException {
        final int value = 10;
        final LiveData<StepCount> stepCount = mRepository.addStepCount(value);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final StepCount liveDataValue = getLiveDataValue(stepCount);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getStepCount(), is(value));
    }

    @Test
    public void getStepCounts() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(stepCountDao.loadStepCounts(any(), any())).thenReturn(liveData);

        final LiveData<Integer> stepCountsToday = mRepository.getStepCountsToday();

        final Integer liveDataValue = getLiveDataValue(stepCountsToday);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(value));
        verify(stepCountDao, atLeastOnce()).loadStepCounts(any(), any());
    }

    @Test
    public void getRemainingStepCounts() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(stepCountDao.loadRemainingStepCount(any(), any())).thenReturn(liveData);

        final LiveData<Integer> data = mRepository.getRemainingStepCountsToday();

        final Integer liveDataValue = getLiveDataValue(data);
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

        final Integer liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(0));
    }

    @Test
    public void addAppUsage() throws InterruptedException {
        final int value = 10;
        final LiveData<AppUsage> appUsage = mRepository.addAppUsage("test", value);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final AppUsage liveDataValue = getLiveDataValue(appUsage);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getTime(), is(value));
    }

    @Test
    public void getAppUsageTime() throws Exception {
        final List<AppUsageEntity> list = new ArrayList<>();

        final AppUsageEntity entity = new AppUsageEntity();
        entity.setId(0);
        entity.setTimestamp(new Date());
        entity.setTime(10);
        entity.setAppName("Test");
        list.add(entity);

        final AppUsageEntity entity1 = new AppUsageEntity();
        entity1.setId(1);
        entity1.setTimestamp(new Date());
        entity1.setTime(7);
        entity1.setAppName("Test1");
        list.add(entity1);

        final MutableLiveData<List<AppUsageEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(list);
        when(appUsageDao.loadAppUsageTime(any(), any())).thenReturn(liveData);

        final LiveData<Map<String, Integer>> data = mRepository.getAppUsageTimeToday();

        assertThat(getLiveDataValue(data), is(notNullValue()));
        assertThat(getLiveDataValue(data).size(), is(2));
        assertThat(getLiveDataValue(data).keySet(), hasItems("Test", "Test1"));
        assertThat(getLiveDataValue(data).values(), hasItems(10, 7));
        verify(appUsageDao, atLeastOnce()).loadAppUsageTime(any(), any());
    }

    @Test
    public void getAppUsagePercent() throws Exception {
        final List<AppUsageEntity> list = new ArrayList<>();

        final AppUsageEntity entity = new AppUsageEntity();
        entity.setId(0);
        entity.setTimestamp(new Date());
        entity.setTime(90);
        entity.setAppName("Test");
        list.add(entity);

        final AppUsageEntity entity1 = new AppUsageEntity();
        entity1.setId(1);
        entity1.setTimestamp(new Date());
        entity1.setTime(10);
        entity1.setAppName("Test1");
        list.add(entity1);

        final MutableLiveData<List<AppUsageEntity>> liveData = new MutableLiveData<>();
        liveData.setValue(list);
        when(appUsageDao.loadAppUsagePercent(any(), any())).thenReturn(liveData);

        final LiveData<Map<String, Double>> data = mRepository.getAppUsagePercentToday();

        final Map<String, Double> liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(2));
        assertThat(liveDataValue.keySet(), hasItems("Test", "Test1"));
        assertThat(liveDataValue.values(), hasItems(0.90, 0.10));
        verify(appUsageDao, atLeastOnce()).loadAppUsagePercent(any(), any());
    }

    @Test
    public void getRemainingAppUsageTime() throws InterruptedException {
        final int value = 10;
        final MutableLiveData<Integer> liveData = new MutableLiveData<>();
        liveData.setValue(value);
        when(appUsageDao.loadRemainingAppUsageTime(any(), any())).thenReturn(liveData);

        final LiveData<Integer> data = mRepository.getRemainingAppUsageTimeToday();

        final Integer liveDataValue = getLiveDataValue(data);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(10));
    }

    @Test
    public void addEmotion() throws InterruptedException {
        final Emotions value = Emotions.NEUTRAL;
        final LiveData<Emotion> emotion = mRepository.addEmotion(value);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        final Emotion liveDataValue = getLiveDataValue(emotion);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.getValue(), is(value));
    }

    @Test
    public void getAllEmotions() throws InterruptedException {
        final List<EmotionEntity> emotionEntities = new ArrayList<>();
        emotionEntities.add(new EmotionEntity());
        emotionEntities.add(new EmotionEntity());
        emotionEntities.add(new EmotionEntity());
        emotionEntities.add(new EmotionEntity());

        final MutableLiveData<List<EmotionEntity>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(emotionEntities);

        when(emotionDao.getAll(any(), any())).thenReturn(mutableLiveData);

        final LiveData<List<Emotion>> allEmotions = mRepository.getAllEmotions(new Date());

        final List<Emotion> liveDataValue = getLiveDataValue(allEmotions);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(emotionEntities.size()));
    }

    @Test
    public void getAverageEmotionEmpty() throws InterruptedException {
        final MutableLiveData<Double> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(null);

        when(emotionDao.getAverageEmotion(any(), any())).thenReturn(mutableLiveData);

        // Ordinary sum is 8.
        // This divided by 5 (amount of items).
        // The result is 1,6 -> round up to 2.
        // Which means Emotions (ordinary) NEUTRAL is the result.

        final LiveData<Emotions> emotion = mRepository.getAverageEmotion(new Date());

        final Emotions liveDataValue = getLiveDataValue(emotion);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(Emotions.NEUTRAL));
    }

    @Test
    public void getAverageEmotion() throws InterruptedException {
        final MutableLiveData<Double> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(2.6);

        when(emotionDao.getAverageEmotion(any(), any())).thenReturn(mutableLiveData);

        // 2,6 -> round up to 3.
        // Which means Emotions (ordinary) HAPPINESS is the result.

        final LiveData<Emotions> emotion = mRepository.getAverageEmotion(new Date());

        final Emotions liveDataValue = getLiveDataValue(emotion);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue, is(Emotions.HAPPINESS));
    }

    @Test
    public void getSummary() throws InterruptedException {
        final List<SummaryEntity> summaryEntities = new ArrayList<>();
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());
        summaryEntities.add(new SummaryEntity());

        final MutableLiveData<List<SummaryEntity>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(summaryEntities);

        when(summaryDao.getSummaries(any(), any())).thenReturn(mutableLiveData);

        final Date date = new Date();
        final LiveData<List<Summary>> summary = mRepository.getSummary(date, date);

        final List<Summary> liveDataValue = getLiveDataValue(summary);
        assertThat(liveDataValue, is(notNullValue()));
        assertThat(liveDataValue.size(), is(summaryEntities.size()));
    }
}
