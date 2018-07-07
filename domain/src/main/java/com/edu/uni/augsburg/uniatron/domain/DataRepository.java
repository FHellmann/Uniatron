package com.edu.uni.augsburg.uniatron.domain;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;
import com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;
import com.edu.uni.augsburg.uniatron.domain.util.AsyncTaskWrapper;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.Emotion;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.LearningAid;
import com.edu.uni.augsburg.uniatron.model.StepCount;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.getMaxTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.getMinTimeOfDate;

/**
 * The data repository wraps the database/service interaction.
 *
 * @author Fabio Hellmann
 */
public final class DataRepository {
    private static final int YEAR_1990 = 1990;
    private final AppDatabase mDatabase;

    /**
     * ctr.
     *
     * @param database The data store.
     */
    public DataRepository(@NonNull final AppDatabase database) {
        mDatabase = database;
    }

    /**
     * Add a new time credit.
     *
     * @param timeCredit The time credit will be generated out of this.
     * @param factor     The factor to multiply with.
     * @return The time credit.
     */
    public LiveData<TimeCredit> addTimeCredit(@NonNull final TimeCredit timeCredit,
                                              final double factor) {
        final MutableLiveData<TimeCredit> observable = new MutableLiveData<>();
        new AsyncTaskWrapper<>(
                () -> {
                    final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
                    timeCreditEntity.setTime(timeCredit.getTime());
                    timeCreditEntity.setStepCount((int) (timeCredit.getStepCount() * factor));
                    timeCreditEntity.setTimestamp(new Date());
                    timeCreditEntity.setType(timeCredit.getType());
                    mDatabase.timeCreditDao().add(timeCreditEntity);
                    return timeCreditEntity;
                },
                observable::setValue
        ).execute();
        return observable;
    }

    /**
     * Check whether the learning aid is active or not.
     *
     * @return The difference in time to the latest learning aid.
     * @see TimeCreditDao#getLatestLearningAid()
     */
    @NonNull
    public LiveData<LearningAid> getLatestLearningAid() {
        return Transformations.map(
                mDatabase.timeCreditDao().getLatestLearningAid(),
                data -> {
                    final long timePassed = data == null
                            ? 0 : System.currentTimeMillis() - data.getTime();
                    final long timeLeft = TimeCredits.CREDIT_LEARNING.getBlockedMinutes()
                            - TimeUnit.MINUTES.convert(timePassed, TimeUnit.MILLISECONDS);
                    if (timeLeft > 0
                            && timeLeft <= TimeCredits.CREDIT_LEARNING.getBlockedMinutes()) {
                        return new LearningAid(timePassed > 0, timeLeft);
                    }
                    return new LearningAid(false, 0);
                });
    }

    /**
     * Get the time credits for today.
     *
     * @return The time credits value.
     */
    @NonNull
    public LiveData<Integer> getTimeCreditsToday() {
        return getTimeCreditsByDate(new Date());
    }

    /**
     * Get the time credits for a specific date.
     *
     * @param date The date to get the time credits from.
     * @return The time credits value.
     */
    @NonNull
    private LiveData<Integer> getTimeCreditsByDate(@NonNull final Date date) {
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return mDatabase.timeCreditDao().loadTimeCredits(dateFrom, dateTo);
    }

    /**
     * Add an amount of steps.
     *
     * @param stepCount The amount of steps.
     * @return The step count.
     */
    public LiveData<StepCount> addStepCount(final int stepCount) {
        final MutableLiveData<StepCount> observable = new MutableLiveData<>();
        new AsyncTaskWrapper<>(
                () -> {
                    final StepCountEntity stepCountEntity = new StepCountEntity();
                    stepCountEntity.setStepCount(stepCount);
                    stepCountEntity.setTimestamp(new Date());
                    mDatabase.stepCountDao().add(stepCountEntity);
                    return stepCountEntity;
                },
                observable::setValue
        ).execute();
        return observable;
    }

    /**
     * Get the step counts for today.
     *
     * @return The amount of steps.
     */
    @NonNull
    public LiveData<Integer> getStepCountsToday() {
        return getStepCountsByDate(new Date());
    }

    /**
     * Get the step counts by date.
     *
     * @param date The date to get the steps from.
     * @return The amount of steps.
     */
    @NonNull
    private LiveData<Integer> getStepCountsByDate(@NonNull final Date date) {
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return mDatabase.stepCountDao().loadStepCounts(dateFrom, dateTo);
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The amount of steps.
     */
    @NonNull
    public LiveData<Integer> getRemainingStepCountsToday() {
        final Date date = new Date();
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return getRemainingStepCountsByDate(dateFrom, dateTo);
    }

    /**
     * Get the remaining step count by date.
     *
     * @param dateFrom The date to get the remaining steps from.
     * @param dateTo   The date to get the remaining steps to.
     * @return The amount of steps.
     */
    @NonNull
    private LiveData<Integer> getRemainingStepCountsByDate(@NonNull final Date dateFrom,
                                                           @NonNull final Date dateTo) {
        return Transformations.map(
                mDatabase.stepCountDao().loadRemainingStepCount(dateFrom, dateTo),
                data -> data > 0 ? data : 0
        );
    }

    /**
     * Add the usage time of an app.
     *
     * @param appName The name of the app which was used.
     * @param time    The time of usage.
     * @return The app usage data.
     */
    public LiveData<AppUsage> addAppUsage(@NonNull final String appName,
                                          final int time) {
        final MutableLiveData<AppUsage> observable = new MutableLiveData<>();
        new AsyncTaskWrapper<>(
                () -> {
                    final AppUsageEntity appUsageEntity = new AppUsageEntity();
                    appUsageEntity.setAppName(appName);
                    appUsageEntity.setTimestamp(new Date());
                    appUsageEntity.setTime(time);
                    mDatabase.appUsageDao().add(appUsageEntity);
                    return appUsageEntity;
                },
                observable::setValue
        ).execute();
        return observable;
    }

    /**
     * Get the total amount of days since the app was installed.
     *
     * @return The amount of days.
     */
    @NonNull
    public LiveData<Date> getMinDate() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(YEAR_1990, 0, 1);
        final Date dateFrom = getMinTimeOfDate(calendar.getTime());
        final Date dateTo = getMaxTimeOfDate(new Date());
        return mDatabase.appUsageDao().getMinDate(dateFrom, dateTo);
    }

    /**
     * Get the app usage time for today.
     *
     * @return The app usage time.
     */
    @NonNull
    public LiveData<Map<String, Integer>> getAppUsageTimeToday() {
        final Date date = new Date();
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return getAppUsageTimeByDate(dateFrom, dateTo);
    }

    /**
     * Get the app usage time from date.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The app usage time.
     */
    @NonNull
    public LiveData<Map<String, Integer>> getAppUsageTimeByDate(@NonNull final Date dateFrom,
                                                                @NonNull final Date dateTo) {
        return Transformations.map(
                mDatabase.appUsageDao().loadAppUsageTime(dateFrom, dateTo),
                appUsageList -> {
                    final HashMap<String, Integer> map = new HashMap<>();
                    for (final AppUsage usage : appUsageList) {
                        map.put(usage.getAppName(), usage.getTime());
                    }
                    return map;
                });
    }

    /**
     * Get the app usage time in percent for today.
     *
     * @return The app usage time in percent.
     */
    @NonNull
    public LiveData<Map<String, Double>> getAppUsagePercentToday() {
        final Date date = new Date();
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return getAppUsagePercentByDate(dateFrom, dateTo);
    }

    /**
     * Get the app usage time in percent from date.
     *
     * @param dateFrom The app usage time in percent from date.
     * @return The app usage time in percent.
     */
    @NonNull
    private LiveData<Map<String, Double>> getAppUsagePercentByDate(@NonNull final Date dateFrom,
                                                                   @NonNull final Date dateTo) {
        return Transformations.map(
                mDatabase.appUsageDao().loadAppUsagePercent(dateFrom, dateTo),
                appUsageList -> {
                    final HashMap<String, Double> map = new HashMap<>();
                    for (final AppUsage usage : appUsageList) {
                        map.put(usage.getAppName(), usage.getTime() / 100.0);
                    }
                    return map;
                });
    }

    /**
     * Get the remaining usage time for today.
     *
     * @param filter The apps to filter.
     * @return The remaining usage time.
     */
    @NonNull
    public LiveData<Integer> getRemainingAppUsageTimeToday(@NonNull final Set<String> filter) {
        final Date date = new Date();
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return getRemainingAppUsageTimeByDate(dateFrom, dateTo, filter);
    }

    /**
     * Get the remaining usage time as sum.
     *
     * @param dateFrom The date to start searching from.
     * @param dateTo   The date to end searching.
     * @param filter   The apps to filter.
     * @return The remaining usage time.
     */
    @NonNull
    private LiveData<Integer> getRemainingAppUsageTimeByDate(@NonNull final Date dateFrom,
                                                             @NonNull final Date dateTo,
                                                             @NonNull final Set<String> filter) {
        return mDatabase.appUsageDao()
                .loadRemainingAppUsageTimeByBlacklist(dateFrom, dateTo, filter);
    }

    /**
     * Add the emotion.
     *
     * @param emotions The emotion to add.
     * @return The emotion data.
     */
    @NonNull
    public LiveData<Emotion> addEmotion(@NonNull final Emotions emotions) {
        final MutableLiveData<Emotion> observable = new MutableLiveData<>();
        new AsyncTaskWrapper<>(
                () -> {
                    final EmotionEntity emotionEntity = new EmotionEntity();
                    emotionEntity.setTimestamp(new Date());
                    emotionEntity.setValue(emotions);
                    mDatabase.emotionDao().add(emotionEntity);
                    return emotionEntity;
                },
                observable::setValue
        ).execute();
        return observable;
    }

    /**
     * Get the emotions for a specified date.
     *
     * @param date The date to get this data from.
     * @return The emotions.
     */
    public LiveData<List<Emotion>> getAllEmotions(@NonNull final Date date) {
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return Transformations.map(
                mDatabase.emotionDao().getAll(dateFrom, dateTo),
                data -> {
                    if (data == null) {
                        return Collections.emptyList();
                    } else {
                        return Stream.of(data).map(item -> item).collect(Collectors.toList());
                    }
                }
        );
    }

    /**
     * Get the average emotion for a specified date.
     *
     * @param date The date to get this data from.
     * @return The average emotion for a date.
     */
    public LiveData<Emotions> getAverageEmotion(@NonNull final Date date) {
        final Date dateFrom = getMinTimeOfDate(date);
        final Date dateTo = getMaxTimeOfDate(date);
        return Transformations.map(
                mDatabase.emotionDao().getAverageEmotion(dateFrom, dateTo),
                data -> data == null ? Emotions.NEUTRAL : Emotions.getAverage(data)
        );
    }

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    public LiveData<List<Summary>> getSummaryByDate(@NonNull final Date dateFrom,
                                                    @NonNull final Date dateTo) {
        return getSummary(dateFrom, dateTo, mDatabase.summaryDao()::getSummariesByDate);
    }

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    public LiveData<List<Summary>> getSummaryByMonth(@NonNull final Date dateFrom,
                                                     @NonNull final Date dateTo) {
        return getSummary(dateFrom, dateTo, mDatabase.summaryDao()::getSummariesByMonth);
    }

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    public LiveData<List<Summary>> getSummaryByYear(@NonNull final Date dateFrom,
                                                    @NonNull final Date dateTo) {
        return getSummary(dateFrom, dateTo, mDatabase.summaryDao()::getSummariesByYear);
    }

    private LiveData<List<Summary>> getSummary(@NonNull final Date dateFrom,
                                               @NonNull final Date dateTo,
                                               @NonNull final BiFunction<Date, Date,
                                                       LiveData<List<SummaryEntity>>> function) {
        final Date dateFromMin = getMinTimeOfDate(dateFrom);
        final Date dateToMax = getMaxTimeOfDate(dateTo);
        return Transformations.map(
                function.apply(dateFromMin, dateToMax),
                data -> data == null ? Collections.emptyList()
                        : Stream.of(data).collect(Collectors.toList())
        );
    }
}
