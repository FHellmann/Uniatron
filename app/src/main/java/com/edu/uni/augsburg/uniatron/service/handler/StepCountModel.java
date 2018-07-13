package com.edu.uni.augsburg.uniatron.service.handler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link StepCountDetector}.
 *
 * @author Fabio Hellmann
 */
public class StepCountModel {

    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param context The context.
     */
    StepCountModel(@NonNull final Context context) {
        mRepository = MainApplication.getRepository(context);
    }

    /**
     * Adds steps to the backend.
     *
     * @param steps The steps to add.
     */
    public void addSteps(final int steps) {
        mRepository.addStepCount(steps);
    }
}
