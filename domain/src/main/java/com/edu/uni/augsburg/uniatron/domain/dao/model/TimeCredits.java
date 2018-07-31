package com.edu.uni.augsburg.uniatron.domain.dao.model;

import java.util.concurrent.TimeUnit;

/**
 * The declared credits for {@link TimeCredit}.
 *
 * @author Fabio Hellmann
 */
public enum TimeCredits implements TimeCredit {
    /**
     * The mTimeBonus credit for 10000 steps.
     */
    CREDIT_10000(10_000, TimeUnit.MILLISECONDS.convert(100, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 7500 steps.
     */
    CREDIT_7500(7_500, TimeUnit.MILLISECONDS.convert(75, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 5000 steps.
     */
    CREDIT_5000(5_000, TimeUnit.MILLISECONDS.convert(50, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 4000 steps.
     */
    CREDIT_4000(4_000, TimeUnit.MILLISECONDS.convert(40, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 3000 steps.
     */
    CREDIT_3000(3_000, TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 2000 steps.
     */
    CREDIT_2000(2_000, TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 1000 steps.
     */
    CREDIT_1000(1_000, TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 500 steps.
     */
    CREDIT_500(500, TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for 100 steps.
     */
    CREDIT_100(100, TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES)),
    /**
     * The mTimeBonus credit for a learning aid.
     */
    CREDIT_LEARNING(TimeCreditType.LEARNING_AID, 0,
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES),
            TimeUnit.MILLISECONDS.convert(55, TimeUnit.MINUTES));

    private final TimeCreditType mType;
    private final int mStepCount;
    private final long mTimeBonus;
    private final long mTimeBlocked;

    TimeCredits(final int stepCount, final long timeBonus) {
        this(TimeCreditType.MINUTES_FOR_STEPS, stepCount, timeBonus, 0);
    }

    TimeCredits(final TimeCreditType type,
                final int stepCount,
                final long timeBonus,
                final long timeBlocked) {
        this.mType = type;
        this.mStepCount = stepCount;
        this.mTimeBonus = timeBonus;
        this.mTimeBlocked = timeBlocked;
    }

    @Override
    public int getStepCount() {
        return mStepCount;
    }

    @Override
    public long getTimeBonus() {
        return mTimeBonus;
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
    public long getBlockedTime() {
        return mTimeBlocked;
    }
}
