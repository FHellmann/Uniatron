package com.edu.uni.augsburg.uniatron.domain;

import android.arch.lifecycle.LiveData;
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
import com.edu.uni.augsburg.uniatron.domain.util.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.util.LiveDataAsyncTask;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.AppUsageCollection;
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
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        return LiveDataAsyncTask.execute(() -> {
            final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
            timeCreditEntity.setTimeBonus(timeCredit.getTimeBonus());
            timeCreditEntity.setStepCount((int) (timeCredit.getStepCount() * factor));
            timeCreditEntity.setTimestamp(new Date());
            timeCreditEntity.setType(timeCredit.getType());
            mDatabase.timeCreditDao().add(timeCreditEntity);
            return timeCreditEntity;
        });
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
                    final long timeLeft = TimeCredits.CREDIT_LEARNING.getBlockedTime()
                            - TimeUnit.MINUTES.convert(timePassed, TimeUnit.MILLISECONDS);
                    if (timeLeft > 0
                            && timeLeft <= TimeCredits.CREDIT_LEARNING.getBlockedTime()) {
                        return new LearningAid(timePassed > 0, timeLeft);
                    }
                    return new LearningAid(false, 0);
                });
    }

    /**
     * Add an amount of steps.
     *
     * @param stepCount The amount of steps.
     * @return The step count.
     */
    public LiveData<StepCount> addStepCount(final int stepCount) {
        return LiveDataAsyncTask.execute(() -> {
            final StepCountEntity stepCountEntity = new StepCountEntity();
            stepCountEntity.setStepCount(stepCount);
            stepCountEntity.setTimestamp(new Date());
            mDatabase.stepCountDao().add(stepCountEntity);
            return stepCountEntity;
        });
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The amount of steps.
     */
    @NonNull
    public LiveData<Integer> getRemainingStepCountsToday() {
        final Date dateFrom = DateConverter.DATE_MIN_TIME.now();
        final Date dateTo = DateConverter.DATE_MAX_TIME.now();
        return Transformations.map(
                mDatabase.stepCountDao().loadRemainingStepCount(dateFrom, dateTo),
                data -> data > 0 ? data : 0
        );
    }

    /**
     * Add the usage time of an app.
     *
     * @param appName   The name of the app which was used.
     * @param usageTime The time of usage.
     * @return The app usage data.
     */
    public LiveData<AppUsage> addAppUsage(@NonNull final String appName,
                                          final long usageTime) {
        return LiveDataAsyncTask.execute(() -> {
            final AppUsageEntity appUsageEntity = new AppUsageEntity();
            appUsageEntity.setAppName(appName);
            appUsageEntity.setTimestamp(new Date());
            appUsageEntity.setUsageTime(usageTime);
            mDatabase.appUsageDao().add(appUsageEntity);
            return appUsageEntity;
        });
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
        final Date dateFrom = DateConverter.DATE_MIN_TIME.convert(calendar.getTime());
        final Date dateTo = DateConverter.DATE_MAX_TIME.convert(new Date());
        return mDatabase.appUsageDao().getMinDate(dateFrom, dateTo);
    }

    /**
     * Get the app usage time from date.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The app usage time.
     */
    @NonNull
    public LiveData<AppUsageCollection> getAppUsageTimeByDate(@NonNull final Date dateFrom,
                                                              @NonNull final Date dateTo) {
        return Transformations.map(mDatabase.appUsageDao().loadAppUsageTime(dateFrom, dateTo),
                appUsageList -> new AppUsageCollection(Stream.ofNullable(appUsageList)
                        .collect(Collectors.toMap(AppUsageEntity::getAppName, AppUsageEntity::getUsageTime))));
    }

    /**
     * Get the remaining usage time for today.
     *
     * @param filter The apps to filter.
     * @return The remaining usage time.
     */
    @NonNull
    public LiveData<Long> getRemainingAppUsageTimeToday(@NonNull final Set<String> filter) {
        final Date dateFrom = DateConverter.DATE_MIN_TIME.now();
        final Date dateTo = DateConverter.DATE_MAX_TIME.now();
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
    private LiveData<Long> getRemainingAppUsageTimeByDate(@NonNull final Date dateFrom,
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
        return LiveDataAsyncTask.execute(() -> {
            final EmotionEntity emotionEntity = new EmotionEntity();
            emotionEntity.setTimestamp(new Date());
            emotionEntity.setValue(emotions);
            mDatabase.emotionDao().add(emotionEntity);
            return emotionEntity;
        });
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
        return Transformations.map(
                function.apply(dateFrom, dateTo),
                data -> data == null ? Collections.emptyList()
                        : Stream.of(data).collect(Collectors.toList())
        );
    }
}
