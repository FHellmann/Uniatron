package com.edu.uni.augsburg.uniatron.service;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * A detector for any listener/receiver.
 *
 * @author Fabio Hellmann
 */
public interface Detector {
    /**
     * Starts the detector.
     *
     * @param context The context.
     */
    void start(@NonNull Context context);

    /**
     * Destroys the detector.
     *
     * @param context The context.
     */
    void destroy(@NonNull Context context);
}
