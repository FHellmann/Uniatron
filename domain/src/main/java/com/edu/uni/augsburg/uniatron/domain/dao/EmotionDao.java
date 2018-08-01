package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotion;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotions;

/**
 * The dao to operate on the emotion table.
 *
 * @author Fabio Hellmann
 */
public interface EmotionDao {

    /**
     * Add the emotion.
     *
     * @param emotions The emotion to add.
     * @return The emotion data.
     */
    LiveData<Emotion> addEmotion(@NonNull Emotions emotions);

    /**
     * Creates a new instance to access the emotion data.
     *
     * @param queryProvider The query provider.
     * @return The emotion dao.
     */
    static EmotionDao create(@NonNull final QueryProvider queryProvider) {
        return new EmotionDaoImpl(queryProvider.emotionQuery());
    }
}
