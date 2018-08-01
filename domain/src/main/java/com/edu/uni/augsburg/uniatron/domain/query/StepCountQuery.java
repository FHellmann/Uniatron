package com.edu.uni.augsburg.uniatron.domain.query;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.table.StepCountEntity;

import java.util.Date;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The dao contains all the calls depending to step count.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverterUtil.class})
public interface StepCountQuery {
    /**
     * Persist a step count.
     *
     * @param stepCount The step count to persist.
     */
    @Insert(onConflict = REPLACE)
    void add(StepCountEntity stepCount);

    /**
     * Load the remaining step count for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The step count.
     */
    @Query("SELECT TOTAL(step_count) - (SELECT TOTAL(steps) FROM TimeCreditEntity "
            + "WHERE TimeCreditEntity.timestamp BETWEEN :dateFrom AND :dateTo) "
            + "FROM StepCountEntity "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo")
    LiveData<Integer> loadRemainingStepCount(Date dateFrom, Date dateTo);
}
