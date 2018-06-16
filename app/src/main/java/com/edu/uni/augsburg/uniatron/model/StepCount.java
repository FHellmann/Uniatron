package com.edu.uni.augsburg.uniatron.model;

import java.util.Date;

/**
 * The interface for the model
 * {@link com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity}.
 *
 * @author Fabio Hellmann
 */
public interface StepCount {
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
     * Get the step count.
     *
     * @return The step count.
     */
    int getStepCount();
}
