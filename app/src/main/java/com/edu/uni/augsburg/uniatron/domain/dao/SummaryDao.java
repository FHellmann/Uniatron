package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverterUtil;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;

import java.util.Date;
import java.util.List;

/**
 * The dao contains all the calls depending to the summary.
 *
 * @author Fabio Hellmann
 */
@Dao
@TypeConverters({DateConverterUtil.class})
public interface SummaryDao {

    /**
     * Get the summary for each day of a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo The date to end searching.
     * @return The summaries.
     */
    @Query("SELECT MAX(timestamp) as 'mTimestamp', "
            + "TOTAL(step_count) as 'mSteps', "
            + "(SELECT TOTAL(usage_time_in_seconds) FROM AppUsageEntity "
            + "WHERE date(timestamp/1000, 'unixepoch') = date(sce.timestamp/1000, 'unixepoch')) "
            + "as 'mAppUsageTime', (SELECT CASE WHEN AVG(value) IS NULL THEN -1.0 ELSE AVG(value) END "
            + "FROM EmotionEntity WHERE date(timestamp/1000, 'unixepoch') = "
            + "date(sce.timestamp/1000, 'unixepoch')) as 'mEmotionAvg' "
            + "FROM StepCountEntity sce "
            + "WHERE timestamp BETWEEN :dateFrom AND :dateTo "
            + "GROUP BY date(timestamp/1000, 'unixepoch') "
            + "ORDER BY timestamp DESC")
    LiveData<List<SummaryEntity>> getSummaries(Date dateFrom, Date dateTo);
}
