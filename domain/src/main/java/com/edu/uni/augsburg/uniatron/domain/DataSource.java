package com.edu.uni.augsburg.uniatron.domain;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.model.AppUsage;
import com.edu.uni.augsburg.uniatron.model.AppUsageItem;
import com.edu.uni.augsburg.uniatron.model.DataCollection;
import com.edu.uni.augsburg.uniatron.model.Emotion;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.LearningAid;
import com.edu.uni.augsburg.uniatron.model.StepCount;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.model.TimeCredit;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The data source calls the layer which provides the data.
 *
 * @author Fabio Hellmann
 */
public interface DataSource {

    /**
     * Add a new time credit.
     *
     * @param timeCredit The time credit will be generated out of this.
     * @param factor     The factor to multiply with.
     * @return The time credit.
     */
    LiveData<TimeCredit> addTimeCredit(@NonNull TimeCredit timeCredit,
                                       double factor);

    /**
     * Check whether the learning aid is active or not.
     *
     * @return The difference in time to the latest learning aid.
     * @see TimeCreditDao#getLatestLearningAid()
     */
    LiveData<LearningAid> getLatestLearningAid();

    /**
     * Add an amount of steps.
     *
     * @param stepCount The amount of steps.
     * @return The step count.
     */
    LiveData<StepCount> addStepCount(int stepCount);

    /**
     * Get the remaining step count for today.
     *
     * @return The amount of steps.
     */
    LiveData<Integer> getRemainingStepCountsToday();

    /**
     * Add the usage time of an app.
     *
     * @param appName   The name of the app which was used.
     * @param usageTime The time of usage.
     * @return The app usage data.
     */
    LiveData<AppUsage> addAppUsage(@NonNull String appName,
                                   long usageTime);

    /**
     * Get the total amount of days since the app was installed.
     *
     * @return The amount of days.
     */
    LiveData<Date> getMinDate();

    /**
     * Get the app usage time from date.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The app usage time.
     */
    LiveData<DataCollection<AppUsageItem>> getAppUsageTimeByDate(@NonNull Date dateFrom,
                                                                 @NonNull Date dateTo);

    /**
     * Get the remaining usage time for today.
     *
     * @param filter The apps to filter.
     * @return The remaining usage time.
     */
    LiveData<Long> getRemainingAppUsageTimeToday(@NonNull Set<String> filter);

    /**
     * Add the emotion.
     *
     * @param emotions The emotion to add.
     * @return The emotion data.
     */
    LiveData<Emotion> addEmotion(@NonNull Emotions emotions);

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    LiveData<List<Summary>> getSummaryByDate(@NonNull Date dateFrom,
                                             @NonNull Date dateTo);

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    LiveData<List<Summary>> getSummaryByMonth(@NonNull Date dateFrom,
                                              @NonNull Date dateTo);

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    LiveData<List<Summary>> getSummaryByYear(@NonNull Date dateFrom,
                                             @NonNull Date dateTo);
}
