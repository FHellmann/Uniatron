package com.edu.uni.augsburg.uniatron.model;

/**
 * The declared credits for {@link TimeCredit}.
 *
 * @author Fabio Hellmann
 */
public enum TimeCredits {
    /** The time credit for 10000 steps. */
    CREDIT_10000(10_000, 100),
    /** The time credit for 7500 steps. */
    CREDIT_7500(7_500, 75),
    /** The time credit for 5000 steps. */
    CREDIT_5000(5_000, 50),
    /** The time credit for 4000 steps. */
    CREDIT_4000(4_000, 40),
    /** The time credit for 3000 steps. */
    CREDIT_3000(3_000, 30),
    /** The time credit for 2000 steps. */
    CREDIT_2000(2_000, 20),
    /** The time credit for 1000 steps. */
    CREDIT_1000(1_000, 10),
    /** The time credit for 500 steps. */
    CREDIT_500(500, 5),
    /** The time credit for 100 steps. */
    CREDIT_100(100, 1);

    private final int mStepCount;
    private final int mTime;

    TimeCredits(final int stepCount, final int time) {
        this.mStepCount = stepCount;
        this.mTime = time;
    }

    /**
     * Get the step count.
     *
     * @return The step count.
     */
    public int getStepCount() {
        return mStepCount;
    }

    /**
     * Get the time in minutes.
     *
     * @return The time.
     */
    public int getTimeInMinutes() {
        return mTime;
    }

    /**
     * Checks whether this time credit is usable with the
     * specified amount of remaining steps.
     *
     * @param availableSteps The available amount of steps.
     * @return <code>true</code> if this time credit is usable,
     * <code>false</code> otherwise.
     */
    public boolean isUsable(final int availableSteps) {
        return availableSteps >= mStepCount;
    }
}
