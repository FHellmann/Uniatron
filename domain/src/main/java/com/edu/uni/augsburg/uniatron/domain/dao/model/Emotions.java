package com.edu.uni.augsburg.uniatron.domain.dao.model;

/**
 * This is a representation of several emotions.
 *
 * @author Fabio Hellmann
 */
public enum Emotions implements Emotion {
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
}
