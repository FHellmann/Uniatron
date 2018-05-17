package com.edu.uni.augsburg.uniatron.model;

import java.util.Date;

/**
 * The interface for the model
 * {@link com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity}.
 *
 * @author Fabio Hellmann
 */
public interface Summary {
    /**
     * Get the timestamp.
     *
     * @return the timestamp.
     */
    Date getTimestamp();

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
    double getEmotionAvg();

    /**
     * Get the average emotion.
     *
     * @return the average emotion.
     */
    default Emotions getEmotion() {
        if(getEmotionAvg() >= 0) {
            final int emotionIndex = (int) Math.round(getEmotionAvg());
            return Emotions.values()[emotionIndex];
        } else {
            return Emotions.NEUTRAL;
        }
    }
}
