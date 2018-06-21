package com.edu.uni.augsburg.uniatron.model;

import java.util.Date;

/**
 * The declared credits for {@link TimeCredit}.
 *
 * @author Fabio Hellmann
 */
public enum TimeCredits implements TimeCredit {
    /**
     * The mTime credit for 10000 steps.
     */
    CREDIT_10000(10_000, 100),
    /**
     * The mTime credit for 7500 steps.
     */
    CREDIT_7500(7_500, 75),
    /**
     * The mTime credit for 5000 steps.
     */
    CREDIT_5000(5_000, 50),
    /**
     * The mTime credit for 4000 steps.
     */
    CREDIT_4000(4_000, 40),
    /**
     * The mTime credit for 3000 steps.
     */
    CREDIT_3000(3_000, 30),
    /**
     * The mTime credit for 2000 steps.
     */
    CREDIT_2000(2_000, 20),
    /**
     * The mTime credit for 1000 steps.
     */
    CREDIT_1000(1_000, 10),
    /**
     * The mTime credit for 500 steps.
     */
    CREDIT_500(500, 5),
    /**
     * The mTime credit for 100 steps.
     */
    CREDIT_100(100, 1),
    /**
     * The mTime credit for a learning aid.
     */
    CREDIT_LEARNING(TimeCreditType.LEARNING_AID, 0, 5, 45);

    private final TimeCreditType mType;
    private final int mStepCount;
    private final int mTime;
    private final int mTimeBlocked;

    TimeCredits(final int stepCount, final int time) {
        this(TimeCreditType.MINUTES_FOR_STEPS, stepCount, time, 0);
    }

    TimeCredits(final TimeCreditType type,
                final int stepCount,
                final int time,
                final int timeBlocked) {
        this.mType = type;
        this.mStepCount = stepCount;
        this.mTime = time;
        this.mTimeBlocked = timeBlocked;
    }

    @Override
    public long getId() {
        throw new IllegalStateException("Method not supported");
    }

    @Override
    public Date getTimestamp() {
        throw new IllegalStateException("Method not supported");
    }

    @Override
    public int getStepCount() {
        return mStepCount;
    }

    @Override
    public int getTime() {
        return mTime;
    }

    @Override
    public TimeCreditType getType() {
        return mType;
    }

    /**
     * Get the blocked minutes.
     *
     * @return The blocked minutes.
     */
    public int getBlockedMinutes() {
        return mTimeBlocked;
    }
}
