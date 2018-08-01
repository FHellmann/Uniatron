package com.edu.uni.augsburg.uniatron.domain.dao.model;

import com.edu.uni.augsburg.uniatron.domain.table.EmotionEntity;

/**
 * The interface for the model
 * {@link EmotionEntity}.
 *
 * @author Fabio Hellmann
 */
public interface Emotion {
    /**
     * Get the emotion value.
     *
     * @return The emotion.
     */
    Emotions getValue();
}
