package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;
import com.edu.uni.augsburg.uniatron.domain.dao.model.AppUsage;
import com.edu.uni.augsburg.uniatron.domain.dao.model.DataCollection;

import java.util.Date;
import java.util.Set;

/**
 * The dao to operate on the app usage table.
 *
 * @author Fabio Hellmann
 */
public interface AppUsageDao {

    /**
     * Add the usage time of an app.
     *
     * @param packageName   The package name of the app which was used.
     * @param usageTime The time of usage.
     * @return The app usage data.
     */
    LiveData<AppUsage> addAppUsage(@NonNull String packageName, long usageTime);

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
    LiveData<DataCollection<AppUsage>> getAppUsageTimeByDate(@NonNull Date dateFrom, @NonNull Date dateTo);

    /**
     * Get the remaining usage time for today.
     *
     * @param filter The apps to filter.
     * @return The remaining usage time.
     */
    LiveData<Long> getRemainingAppUsageTimeToday(@NonNull Set<String> filter);

    /**
     * Creates a new instance to access the app usage data.
     *
     * @param queryProvider The query provider.
     * @return The app usage dao.
     */
    static AppUsageDao create(@NonNull final QueryProvider queryProvider) {
        return new AppUsageDaoImpl(queryProvider.appUsageQuery());
    }
}
