package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.dao.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.dao.model.StepCount;
import com.edu.uni.augsburg.uniatron.domain.query.StepCountQuery;
import com.edu.uni.augsburg.uniatron.domain.table.StepCountEntity;

import java.util.Calendar;
import java.util.Date;

/**
 * The dao implementation for {@link StepCountDao}.
 *
 * @author Fabio Hellmann
 */
class StepCountDaoImpl implements StepCountDao {

    @NonNull
    private final StepCountQuery mStepCountQuery;

    /**
     * Ctr.
     *
     * @param stepCountQuery The query to access the step count table.
     */
    StepCountDaoImpl(@NonNull final StepCountQuery stepCountQuery) {
        mStepCountQuery = stepCountQuery;
    }

    public LiveData<StepCount> addStepCount(final int stepCount) {
        return LiveDataAsyncTask.execute(() -> {
            final StepCountEntity stepCountEntity = new StepCountEntity();
            stepCountEntity.setStepCount(stepCount);
            stepCountEntity.setTimestamp(new Date());
            mStepCountQuery.add(stepCountEntity);
            return stepCountEntity;
        });
    }

    public LiveData<Integer> getRemainingStepCountsToday() {
        final Date dateFrom = DateConverter.getMin(Calendar.DATE).now();
        final Date dateTo = DateConverter.getMax(Calendar.DATE).now();
        return Transformations.map(
                mStepCountQuery.loadRemainingStepCount(dateFrom, dateTo),
                data -> data > 0 ? data : 0
        );
    }
}
