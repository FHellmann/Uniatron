package com.edu.uni.augsburg.uniatron.ui.home.shop;

/**
 * The model for learning aid.
 *
 * @author Fabio Hellmann
 */
public class LearningAid {
    private final boolean active;
    private final long leftTime;

    /**
     * Ctr.
     *
     * @param active   The state of the learning aid.
     * @param leftTime The time left.
     */
    LearningAid(final boolean active, final long leftTime) {
        this.active = active;
        this.leftTime = leftTime;
    }

    /**
     * Check whether the learning aid is active or not.
     *
     * @return {@code true} if the learning aid is active, {@code false} otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Get the left time for the current learning aid.
     *
     * @return the learning aid left time.
     */
    public long getLeftTime() {
        return leftTime;
    }
}
