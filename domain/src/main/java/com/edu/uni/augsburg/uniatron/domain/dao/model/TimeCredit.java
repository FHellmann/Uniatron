package com.edu.uni.augsburg.uniatron.domain.dao.model;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.domain.table.TimeCreditEntity;

/**
 * The interface for the model
 * {@link TimeCreditEntity}.
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

    /**
     * Get all defined time credits.
     *
     * @return The time credits.
     */
    static Stream<TimeCredit> getAll() {
        return Stream.of(TimeCredits.values());
    }
}
