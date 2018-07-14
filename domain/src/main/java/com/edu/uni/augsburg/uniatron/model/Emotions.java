package com.edu.uni.augsburg.uniatron.model;

/**
 * This is a representation of several emotions.
 *
 * @author Fabio Hellmann
 */
public enum Emotions {
    /**
     * The emotion state ANGRY.
     **/
    ANGRY(0),
    /**
     * The emotion state SADNESS.
     **/
    SADNESS(1),
    /**
     * The emotion state NEUTRAL.
     **/
    NEUTRAL(2),
    /**
     * The emotion state HAPPINESS.
     **/
    HAPPINESS(3),
    /**
     * The emotion state FANTASTIC.
     **/
    FANTASTIC(4);

    private final int mIndex;

    Emotions(final int index) {
        mIndex = index;
    }

    /**
     * Get the index.
     *
     * @return The index.
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Get the average emotion.
     *
     * @param averageEmotion The average value.
     * @return the emotion.
     */
    public static Emotions getAverage(final double averageEmotion) {
        if (averageEmotion >= 0) {
            final int emotionIndex = (int) Math.round(averageEmotion);
            return Emotions.values()[emotionIndex];
        } else {
            return Emotions.NEUTRAL;
        }
    }
}
