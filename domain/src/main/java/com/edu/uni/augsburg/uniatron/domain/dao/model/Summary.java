package com.edu.uni.augsburg.uniatron.domain.dao.model;

import com.edu.uni.augsburg.uniatron.domain.table.SummaryEntity;

/**
 * The interface for the model
 * {@link SummaryEntity}.
 *
 * @author Fabio Hellmann
 */
public interface Summary {
    /**
     * Get the app usage time.
     *
     * @return the app usage time.
     */
    long getAppUsageTime();

    /**
     * Get the step count.
     *
     * @return the step count.
     */
    long getSteps();

    /**
     * Get the average emotion as double.
     *
     * @return the average emotion as double.
     */
    Emotions getEmotion();
}
