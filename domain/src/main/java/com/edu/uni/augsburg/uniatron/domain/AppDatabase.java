package com.edu.uni.augsburg.uniatron.domain;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.converter.EmotionConverter;
import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.EmotionDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.model.EmotionEntity;
import com.edu.uni.augsburg.uniatron.domain.model.StepCountEntity;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;

/**
 * The app database layer.
 *
 * @author Fabio Hellmann
 */
@Database(version = 1, entities = {
        StepCountEntity.class,
        AppUsageEntity.class,
        TimeCreditEntity.class,
        EmotionEntity.class
})
@TypeConverters({DateConverter.class, EmotionConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Get the dao to access the step counts.
     *
     * @return the step count dao.
     */
    public abstract StepCountDao stepCountDao();

    /**
     * Get the app usage dao.
     *
     * @return the app usage dao.
     */
    public abstract AppUsageDao appUsageDao();

    /**
     * Get the time credit dao.
     *
     * @return the time credit dao.
     */
    public abstract TimeCreditDao timeCreditDao();

    /**
     * Get the emotion dao.
     *
     * @return the emotion dao.
     */
    public abstract EmotionDao emotionDao();
}
