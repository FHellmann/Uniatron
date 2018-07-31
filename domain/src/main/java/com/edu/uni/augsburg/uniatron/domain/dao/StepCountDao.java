package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;
import com.edu.uni.augsburg.uniatron.domain.dao.model.StepCount;

/**
 * The dao to operate on the step count table.
 *
 * @author Fabio Hellmann
 */
public interface StepCountDao {

    /**
     * Add an amount of steps.
     *
     * @param stepCount The amount of steps.
     * @return The step count.
     */
    LiveData<StepCount> addStepCount(int stepCount);

    /**
     * Get the remaining step count for today.
     *
     * @return The amount of steps.
     */
    LiveData<Integer> getRemainingStepCountsToday();

    /**
     * Creates a new instance to access the step count data.
     *
     * @param queryProvider The query provider.
     * @return The step count dao.
     */
    static StepCountDao create(@NonNull final QueryProvider queryProvider) {
        return new StepCountDaoImpl(queryProvider.stepCountQuery());
    }
}
