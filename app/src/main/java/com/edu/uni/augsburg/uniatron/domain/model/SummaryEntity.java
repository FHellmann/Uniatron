package com.edu.uni.augsburg.uniatron.domain.model;

import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.Date;

/**
 * A small model for summary to bundle the necessary information.
 *
 * @author Fabio Hellmann
 */
public class SummaryEntity implements Summary {
    private Date mTimestamp;
    private long mAppUsageTime;
    private long mSteps;
    private double mEmotionAvg;

    public Date getTimestamp() {
        return (Date) mTimestamp.clone();
    }

    public void setTimestamp(final Date timestamp) {
        this.mTimestamp = (Date) timestamp.clone();
    }

    public long getAppUsageTime() {
        return mAppUsageTime;
    }

    public void setAppUsageTime(final long appUsageTime) {
        this.mAppUsageTime = appUsageTime;
    }

    public long getSteps() {
        return mSteps;
    }

    public void setSteps(final long steps) {
        this.mSteps = steps;
    }

    public double getEmotionAvg() {
        return mEmotionAvg;
    }

    public void setEmotionAvg(final double emotionAvg) {
        this.mEmotionAvg = emotionAvg;
    }
}
