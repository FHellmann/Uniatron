package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.edu.uni.augsburg.uniatron.domain.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.model.SummaryEntity;

import java.util.Date;
import java.util.List;

@Dao
@TypeConverters({DateConverter.class})
public interface SummaryDao {

    @Query("SELECT date(timestamp) as 'mTimestamp', " +
            "TOTAL(usage_time_in_seconds) as 'mAppUsageTime', " +
            "TOTAL(sce.step_count) as 'mSteps', " +
            "CASE WHEN AVG(ee.value) IS NULL THEN 0.0 ELSE AVG(ee.value) END as 'mEmotionAvg' " +
            "FROM AppUsageEntity, " +
            "(SELECT date(timestamp), step_count FROM StepCountEntity WHERE timestamp BETWEEN :dateFrom AND :dateTo) sce, " +
            "(SELECT date(timestamp), value FROM EmotionEntity WHERE timestamp BETWEEN :dateFrom AND :dateTo) ee " +
            "WHERE timestamp BETWEEN :dateFrom AND :dateTo " +
            "GROUP BY date(timestamp) " +
            "ORDER BY timestamp DESC")
    LiveData<List<SummaryEntity>> getSummaries(Date dateFrom, Date dateTo);
}
