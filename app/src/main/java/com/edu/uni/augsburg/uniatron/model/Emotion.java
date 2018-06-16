package com.edu.uni.augsburg.uniatron.model;

import java.util.Date;

/**
 * The interface for the model
 * {@link com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity}.
 *
 * @author Fabio Hellmann
 */
public interface Emotion {
    /**
     * Get the id.
     *
     * @return The id.
     */
    long getId();

    /**
     * Get the timestamp.
     *
     * @return The timestamp.
     */
    Date getTimestamp();

    /**
     * Get the emotion value.
     *
     * @return The value.
     */
    Emotions getValue();
}
