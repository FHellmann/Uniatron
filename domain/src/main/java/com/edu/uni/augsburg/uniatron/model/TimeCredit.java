package com.edu.uni.augsburg.uniatron.model;

/**
 * The interface for the model
 * {@link com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity}.
 *
 * @author Fabio Hellmann
 */
public interface TimeCredit {
    /**
     * Get the time in minutes.
     *
     * @return The time in minutes.
     */
    long getTimeBonus();

    /**
     * Get the step count.
     *
     * @return The step count.
     */
    int getStepCount();

    /**
     * Get the type.
     *
     * @return The type.
     */
    TimeCreditType getType();
}
