package com.edu.uni.augsburg.uniatron.domain.dao.model;

import com.edu.uni.augsburg.uniatron.domain.table.AppUsageEntity;

/**
 * The interface for the model
 * {@link AppUsageEntity}.
 *
 * @author Fabio Hellmann
 */
public interface AppUsage {
    /**
     * Get the app name.
     *
     * @return The app name.
     */
    String getPackageName();

    /**
     * Get the usage time of the app in seconds.
     *
     * @return The usage time.
     */
    long getUsageTime();

    /**
     * Get the usage time of the app in percent.
     *
     * @return The usage time in percent.
     */
    double getUsageTimeAllPercent();
}
