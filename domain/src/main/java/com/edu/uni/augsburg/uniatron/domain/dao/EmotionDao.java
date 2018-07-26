package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.converter.EmotionConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The dao contains all the calls depending to
 * {@link com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity}.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverterUtil.class, EmotionConverterUtil.class})
public interface EmotionDao {
    /**
     * Persist an emotion.
     *
     * @param emotion The emotion to persist.
     */
    @Insert(onConflict = REPLACE)
    void add(EmotionEntity emotion);
}
