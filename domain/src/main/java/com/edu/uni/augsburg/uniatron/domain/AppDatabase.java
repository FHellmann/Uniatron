package com.edu.uni.augsburg.uniatron.domain;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.converter.EmotionConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.converter.TimeCreditTypeConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.table.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.table.EmotionEntity;
import com.edu.uni.augsburg.uniatron.domain.table.StepCountEntity;
import com.edu.uni.augsburg.uniatron.domain.table.TimeCreditEntity;

/**
 * The app database layer.
 *
 * @author Fabio Hellmann
 */
@Database(
        version = 2,
        entities = {
                StepCountEntity.class,
                AppUsageEntity.class,
                TimeCreditEntity.class,
                EmotionEntity.class
        }
)
@TypeConverters({
        DateConverterUtil.class,
        EmotionConverterUtil.class,
        TimeCreditTypeConverterUtil.class
})
public abstract class AppDatabase extends RoomDatabase implements QueryProvider {
}
