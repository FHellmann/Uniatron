package com.edu.uni.augsburg.uniatron.domain.table;

import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotion;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotions;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Summary;

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

    @Override
    public Emotion getEmotion() {
        if (mEmotionAvg >= 0) {
            final int emotionIndex = (int) Math.round(mEmotionAvg);
            return Emotions.values()[emotionIndex];
        } else {
            return Emotions.NEUTRAL;
        }
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
