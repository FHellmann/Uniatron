package com.edu.uni.augsburg.uniatron.model;

/**
 * The interface for the model
 * {@link com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity}.
 *
 * @author Fabio Hellmann
 */
public interface AppUsage {
    /**
     * Get the app name.
     *
     * @return The app name.
     */
    String getAppName();

    /**
     * Get the usage time of the app in seconds.
     *
     * @return The usage time.
     */
    long getUsageTime();
}
