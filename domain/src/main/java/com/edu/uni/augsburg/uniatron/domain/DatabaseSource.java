package com.edu.uni.augsburg.uniatron.domain;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.EmotionDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.SummaryDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.domain.migration.Migrations;

/**
 * The data source of the database.
 *
 * @author Fabio Hellmann
 */
public interface DatabaseSource {
    /**
     * Get the dao to access the step counts.
     *
     * @return the step count dao.
     */
    StepCountDao stepCountDao();

    /**
     * Get the app usage dao.
     *
     * @return the app usage dao.
     */
    AppUsageDao appUsageDao();

    /**
     * Get the time credit dao.
     *
     * @return the time credit dao.
     */
    TimeCreditDao timeCreditDao();

    /**
     * Get the emotion dao.
     *
     * @return the emotion dao.
     */
    EmotionDao emotionDao();

    /**
     * Get the summary dao.
     *
     * @return the summary dao.
     */
    SummaryDao summaryDao();

    /**
     * Create the database.
     *
     * @param context The app context.
     * @return the database.
     */
    static DatabaseSource create(@NonNull final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, context.getPackageName())
                .addMigrations(Migrations.getAll())
                .build();
    }
}
