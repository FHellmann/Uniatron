package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotion;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Emotions;
import com.edu.uni.augsburg.uniatron.domain.query.EmotionQuery;
import com.edu.uni.augsburg.uniatron.domain.table.EmotionEntity;

import java.util.Date;

/**
 * The dao implementation for {@link EmotionDao}.
 *
 * @author Fabio Hellmann
 */
class EmotionDaoImpl implements EmotionDao {

    @NonNull
    private final EmotionQuery mEmotionQuery;

    /**
     * Ctr.
     *
     * @param emotionQuery The queries to access the emotion table.
     */
    EmotionDaoImpl(@NonNull final EmotionQuery emotionQuery) {
        mEmotionQuery = emotionQuery;
    }

    public LiveData<Emotion> addEmotion(@NonNull final Emotions emotions) {
        return LiveDataAsyncTask.execute(() -> {
            final EmotionEntity emotionEntity = new EmotionEntity();
            emotionEntity.setTimestamp(new Date());
            emotionEntity.setValue(emotions);
            mEmotionQuery.add(emotionEntity);
            return emotionEntity;
        });
    }
}
