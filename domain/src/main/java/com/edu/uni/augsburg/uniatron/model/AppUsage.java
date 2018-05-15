package com.edu.uni.augsburg.uniatron.model;

import java.util.Date;

/**
 * The interface for the model
 * {@link com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity}.
 *
 * @author Fabio Hellmann
 */
public interface AppUsage {
    /**
     * Get the id.
     *
     * @return The id.
     */
    long getId();

    /**
     * Get the app name.
     *
     * @return The app name.
     */
    String getAppName();

    /**
     * Get the timestamp.
     *
     * @return The timestamp.
     */
    Date getTimestamp();

    /**
     * Get the usage time of the app in seconds.
     *
     * @return The usage time.
     */
    int getTime();
}
