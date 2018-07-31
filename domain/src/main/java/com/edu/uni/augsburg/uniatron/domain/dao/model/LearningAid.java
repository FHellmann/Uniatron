package com.edu.uni.augsburg.uniatron.domain.dao.model;

import com.annimon.stream.Optional;

/**
 * The model for learning aid.
 *
 * @author Fabio Hellmann
 */
public interface LearningAid {
    /**
     * Check whether or not the learning aid is active.
     *
     * @return {@code true} if the learning aid is active, {@code false} otherwise.
     */
    default boolean isActive() {
        return getLeftTime().isPresent();
    }

    /**
     * Get the left time for the current learning aid or empty if the learning aid is inactive.
     *
     * @return the learning aid left time.
     */
    Optional<Long> getLeftTime();
}
