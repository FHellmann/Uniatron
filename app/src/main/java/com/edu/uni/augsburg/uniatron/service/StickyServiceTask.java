package com.edu.uni.augsburg.uniatron.service;

/**
 * A task which will be executed when the {@link StickyService} is running.
 *
 * @author Fabio Hellmann
 */
public interface StickyServiceTask {
    /**
     * Is called when the service is created.
     */
    void onCreate();

    /**
     * Is called when the service is destroyed.
     */
    void onDestroy();
}
