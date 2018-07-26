package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * The dao contains all the calls depending to app usage.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverterUtil.class})
public interface AppUsageDao {
    /**
     * Persist an app usage.
     *
     * @param appUsageEntity The app usage to persist.
     */
    @Insert(onConflict = REPLACE)
    void add(AppUsageEntity appUsageEntity);

    /**
     * Load the app usage time of each app summed by the app name.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The app usage time by app.
     */
    @Query("SELECT 0 id, app_name, date('now') timestamp, "
            + "TOTAL(usage_time) usage_time "
            + "FROM AppUsageEntity "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo "
            + "GROUP BY app_name "
            + "ORDER BY TOTAL(usage_time) DESC")
    LiveData<List<AppUsageEntity>> loadAppUsageTime(Date dateFrom, Date dateTo);

    /**
     * Load the remaining app usage time.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @param filter   The filter for observed apps.
     * @return The remaining app usage time.
     */
    @Query("SELECT (SELECT TOTAL(time_bonus) FROM TimeCreditEntity "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo) - TOTAL(usage_time) "
            + "FROM AppUsageEntity WHERE (timestamp BETWEEN :dateFrom AND :dateTo) "
            + "AND app_name IN (:filter)")
    LiveData<Long> loadRemainingAppUsageTimeByBlacklist(Date dateFrom,
                                                        Date dateTo,
                                                        Set<String> filter);

    /**
     * Get the total days for the specified date range.
     *
     * @param dateFrom The date to start counting.
     * @param dateTo   The date to end counting.
     * @return The total days.
     */
    @Query("SELECT timestamp FROM AppUsageEntity WHERE timestamp BETWEEN :dateFrom AND :dateTo "
            + "ORDER BY timestamp ASC LIMIT 1")
    LiveData<Date> getMinDate(Date dateFrom, Date dateTo);
}
