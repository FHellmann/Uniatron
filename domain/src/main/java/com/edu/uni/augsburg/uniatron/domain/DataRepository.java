package com.edu.uni.augsburg.uniatron.domain;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;
import com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;
import com.edu.uni.augsburg.uniatron.domain.util.AsyncTaskWrapper;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.Emotion;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.StepCount;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMaxTimeOfDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtil.extractMinTimeOfDate;

/**
 * The data repository wraps the database/service interaction.
 *
 * @author Fabio Hellmann
 */
public final class DataRepository {
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
     * @param timeCredits The time credit will be generated out of this.
     * @return The time credit.
     */
    public LiveData<TimeCredit> addTimeCredit(@NonNull final TimeCredits timeCredits) {
        final MutableLiveData<TimeCredit> observable = new MutableLiveData<>();
        new AsyncTaskWrapper<>(
                () -> {
                    final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
                    timeCreditEntity.setTime(timeCredits.getTimeInMinutes());
                    timeCreditEntity.setStepCount(timeCredits.getStepCount());
                    timeCreditEntity.setTimestamp(new Date());
                    mDatabase.timeCreditDao().add(timeCreditEntity);
                    return timeCreditEntity;
                },
                observable::setValue
        ).execute();
        return observable;
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
    public LiveData<Integer> getTimeCreditsByDate(@NonNull final Date date) {
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
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
    public LiveData<Integer> getStepCountsByDate(@NonNull final Date date) {
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
        return mDatabase.stepCountDao().loadStepCounts(dateFrom, dateTo);
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The amount of steps.
     */
    @NonNull
    public LiveData<Integer> getRemainingStepCountsToday() {
        return getRemainingStepCountsByDate(new Date());
    }

    /**
     * Get the remaining step count by date.
     *
     * @param date The date to get the remaining steps from.
     * @return The amount of steps.
     */
    @NonNull
    public LiveData<Integer> getRemainingStepCountsByDate(@NonNull final Date date) {
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
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
     * Get the app usage time for today.
     *
     * @return The app usage time.
     */
    @NonNull
    public LiveData<Map<String, Integer>> getAppUsageTimeToday() {
        return getAppUsageTimeByDate(new Date());
    }

    /**
     * Get the app usage time from date.
     *
     * @param date The app usage time from date.
     * @return The app usage time.
     */
    @NonNull
    public LiveData<Map<String, Integer>> getAppUsageTimeByDate(@NonNull final Date date) {
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
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
        return getAppUsagePercentByDate(new Date());
    }

    /**
     * Get the app usage time in percent from date.
     *
     * @param date The app usage time in percent from date.
     * @return The app usage time in percent.
     */
    @NonNull
    public LiveData<Map<String, Double>> getAppUsagePercentByDate(@NonNull final Date date) {
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
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
     * @return The remaining usage time.
     */
    @NonNull
    public LiveData<Integer> getRemainingAppUsageTimeToday() {
        return getRemainingAppUsageTimeByDate(new Date());
    }

    /**
     * Get the remaining usage time as sum.
     *
     * @param date The date to get this data from.
     * @return The remaining usage time.
     */
    @NonNull
    private LiveData<Integer> getRemainingAppUsageTimeByDate(@NonNull final Date date) {
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
        return mDatabase.appUsageDao().loadRemainingAppUsageTime(dateFrom, dateTo);
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
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
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
        final Date dateFrom = extractMinTimeOfDate(date);
        final Date dateTo = extractMaxTimeOfDate(date);
        return Transformations.map(
                mDatabase.emotionDao().getAverageEmotion(dateFrom, dateTo),
                data -> {
                    if (data == null) {
                        return Emotions.NEUTRAL;
                    } else {
                        final int index = (int) Math.round(data);
                        return Emotions.values()[index];
                    }
                }
        );
    }

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    public LiveData<List<Summary>> getSummary(@NonNull final Date dateFrom,
                                              @NonNull final Date dateTo) {
        return Transformations.map(
                mDatabase.summaryDao().getSummaries(dateFrom, dateTo),
                data -> data == null ? Collections.emptyList()
                        : Stream.of(data).collect(Collectors.toList())
        );
    }
}
