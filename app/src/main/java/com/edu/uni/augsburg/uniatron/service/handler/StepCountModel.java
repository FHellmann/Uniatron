package com.edu.uni.augsburg.uniatron.service.handler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;

/**
 * The model is the connection between the data source
 * and the {@link StepCountDetector}.
 *
 * @author Fabio Hellmann
 */
public class StepCountModel {

    private final StepCountDao mStepCountDao;

    /**
     * Ctr.
     *
     * @param context The context.
     */
    StepCountModel(@NonNull final Context context) {
        mStepCountDao = AppContext.getInstance(context).getStepCountDao();
    }

    /**
     * Adds steps to the backend.
     *
     * @param steps The steps to add.
     */
    public void addSteps(final int steps) {
        mStepCountDao.addStepCount(steps);
    }
}
