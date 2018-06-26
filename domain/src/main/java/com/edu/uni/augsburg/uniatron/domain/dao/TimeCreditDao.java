package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.converter.TimeCreditTypeConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.model.TimeCreditEntity;

import java.util.Date;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The dao contains all the calls depending to time credit.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverterUtil.class, TimeCreditTypeConverterUtil.class})
public interface TimeCreditDao {
    /**
     * Persist a time credit.
     *
     * @param timeCreditEntity The time credit to persist.
     */
    @Insert(onConflict = REPLACE)
    void add(TimeCreditEntity timeCreditEntity);

    /**
     * Query the sum of remaining time credits for the current date.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return the remaining time credits.
     */
    @Query("SELECT TOTAL(time_in_minutes) FROM TimeCreditEntity "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo")
    LiveData<Integer> loadTimeCredits(Date dateFrom, Date dateTo);

    /**
     * Query whether the learning aid is active or not.
     *
     * @return The difference in time to the latest learning aid.
     */
    @Query("SELECT timestamp "
            + "FROM TimeCreditEntity "
            + "WHERE type = 'LEARNING_AID' "
            + "ORDER BY timestamp DESC "
            + "LIMIT 1")
    LiveData<Date> getLatestLearningAid();
}
