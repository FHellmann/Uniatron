package com.edu.uni.augsburg.uniatron.model;

import java.util.Date;

/**
 * The interface for the model
 * {@link com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity}.
 *
 * @author Fabio Hellmann
 */
public interface TimeCredit {
    /**
     * Get the id.
     *
     * @return The id.
     */
    long getId();

    /**
     * Get the timestamp.
     *
     * @return The timestamp.
     */
    Date getTimestamp();

    /**
     * Get the time in minutes.
     *
     * @return The time in minutes.
     */
    int getTime();

    /**
     * Get the step count.
     *
     * @return The step count.
     */
    int getStepCount();
}
