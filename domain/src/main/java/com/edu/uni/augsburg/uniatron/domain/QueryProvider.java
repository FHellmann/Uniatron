package com.edu.uni.augsburg.uniatron.domain;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.migration.Migrations;
import com.edu.uni.augsburg.uniatron.domain.query.AppUsageQuery;
import com.edu.uni.augsburg.uniatron.domain.query.EmotionQuery;
import com.edu.uni.augsburg.uniatron.domain.query.StepCountQuery;
import com.edu.uni.augsburg.uniatron.domain.query.SummaryQuery;
import com.edu.uni.augsburg.uniatron.domain.query.TimeCreditQuery;

/**
 * The data source of the database.
 *
 * @author Fabio Hellmann
 */
public interface QueryProvider {
    /**
     * Get the dao to access the step counts.
     *
     * @return the step count dao.
     */
    StepCountQuery stepCountQuery();

    /**
     * Get the app usage dao.
     *
     * @return the app usage dao.
     */
    AppUsageQuery appUsageQuery();

    /**
     * Get the time credit dao.
     *
     * @return the time credit dao.
     */
    TimeCreditQuery timeCreditQuery();

    /**
     * Get the emotion dao.
     *
     * @return the emotion dao.
     */
    EmotionQuery emotionQuery();

    /**
     * Get the summary dao.
     *
     * @return the summary dao.
     */
    SummaryQuery summaryQuery();

    /**
     * Create the database.
     *
     * @param context The app context.
     * @return the database.
     */
    static QueryProvider create(@NonNull final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, context.getPackageName())
                .addMigrations(Migrations.getAll())
                .build();
    }
}
