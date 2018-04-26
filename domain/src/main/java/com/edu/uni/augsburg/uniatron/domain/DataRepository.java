package com.edu.uni.augsburg.uniatron.domain;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.StepCount;
import com.edu.uni.augsburg.uniatron.model.TimeCredit;
import com.edu.uni.augsburg.uniatron.model.TimeCreditItem;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.edu.uni.augsburg.uniatron.domain.util.DateUtils.extractMaxDate;
import static com.edu.uni.augsburg.uniatron.domain.util.DateUtils.extractMinDate;

/**
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
     * @param timeCreditItem The time credit will be generated out of this.
     * @return The time credit.
     */
    public TimeCredit addTimeCredit(@NonNull final TimeCreditItem timeCreditItem) {
        final TimeCreditEntity timeCreditEntity = new TimeCreditEntity();
        timeCreditEntity.setTimeInMinutes(timeCreditItem.getTimeInMinutes());
        timeCreditEntity.setTimestamp(new Date());
        mDatabase.timeCreditDao().add(timeCreditEntity);
        return timeCreditEntity;
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
        final Date dateFrom = extractMinDate(date);
        final Date dateTo = extractMaxDate(date);
        return mDatabase.timeCreditDao().loadTimeCredits(dateFrom, dateTo);
    }

    /**
     * Add an amount of steps.
     *
     * @param stepCount The amount of steps.
     * @return The step count.
     */
    public StepCount addStepCount(final int stepCount) {
        final StepCountEntity stepCountEntity = new StepCountEntity();
        stepCountEntity.setStepCount(stepCount);
        stepCountEntity.setTimestamp(new Date());
        mDatabase.stepCountDao().add(stepCountEntity);
        return stepCountEntity;
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
        final Date dateFrom = extractMinDate(date);
        final Date dateTo = extractMaxDate(date);
        return mDatabase.stepCountDao().loadStepCounts(dateFrom, dateTo);
    }

    /**
     * Add the usage time of an app.
     *
     * @param appName            The name of the app which was used.
     * @param usageTimeInSeconds The time of usage.
     * @return The app usage data.
     */
    public AppUsage addAppUsage(@NonNull final String appName, final int usageTimeInSeconds) {
        final AppUsageEntity appUsageEntity = new AppUsageEntity();
        appUsageEntity.setAppName(appName);
        appUsageEntity.setTimestamp(new Date());
        appUsageEntity.setUsageTimeInSeconds(usageTimeInSeconds);
        mDatabase.appUsageDao().add(appUsageEntity);
        return appUsageEntity;
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
        final Date dateFrom = extractMinDate(date);
        final Date dateTo = extractMaxDate(date);
        return Transformations.map(
                mDatabase.appUsageDao().loadAppUsageTime(dateFrom, dateTo),
                appUsageList -> {
                    final HashMap<String, Integer> map = new HashMap<>();
                    for (AppUsage usage : appUsageList) {
                        map.put(usage.getAppName(), usage.getUsageTimeInSeconds());
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
        final Date dateFrom = extractMinDate(date);
        final Date dateTo = extractMaxDate(date);
        return Transformations.map(
                mDatabase.appUsageDao().loadAppUsagePercent(dateFrom, dateTo),
                appUsageList -> {
                    final HashMap<String, Double> map = new HashMap<>();
                    for (AppUsage usage : appUsageList) {
                        map.put(usage.getAppName(), usage.getUsageTimeInSeconds() / 100.0);
                    }
                    return map;
                });
    }
}
