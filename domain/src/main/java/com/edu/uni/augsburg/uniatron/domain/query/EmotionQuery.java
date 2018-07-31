package com.edu.uni.augsburg.uniatron.domain.query;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.converter.EmotionConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.table.EmotionEntity;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The dao contains all the calls depending to
 * {@link EmotionEntity}.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverterUtil.class, EmotionConverterUtil.class})
public interface EmotionQuery {
    /**
     * Persist an emotion.
     *
     * @param emotion The emotion to persist.
     */
    @Insert(onConflict = REPLACE)
    void add(EmotionEntity emotion);
}
