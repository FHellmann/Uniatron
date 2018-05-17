package com.edu.uni.augsburg.uniatron.model;

/**
 * This is a representation of several emotions.
 *
 * @author Fabio Hellmann
 */
public enum Emotions {
    /** The emotion state ANGRY. **/
    ANGRY,
    /** The emotion state SADNESS. **/
    SADNESS,
    /** The emotion state NEUTRAL. **/
    NEUTRAL,
    /** The emotion state HAPPINESS. **/
    HAPPINESS,
    /** The emotion state FANTASTIC. **/
    FANTASTIC;

    /**
     * Get the average emotion.
     *
     * @param averageEmotion The average value.
     * @return the emotion.
     */
    public static Emotions getAverage(final double averageEmotion) {
        if(averageEmotion >= 0) {
            final int emotionIndex = (int) Math.round(averageEmotion);
            return Emotions.values()[emotionIndex];
        } else {
            return Emotions.NEUTRAL;
        }
    }
}
