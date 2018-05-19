package com.edu.uni.augsburg.uniatron;

import android.app.Application;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

/**
 * The application context for this app.
 *
 * @author Fabio Hellmann
 */
public class MainApplication extends Application {
    private DataRepository mDataRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize app database
        mDataRepository = new DataRepository(AppDatabase.create(this));

        // initialize explicit broadcast receivers -> See class description
        BroadcastReceiverUtil.registerReceivers(this);
    }

    /**
     * Get the data repository.
     *
     * @return The data repository.
     */
    public DataRepository getRepository() {
        return mDataRepository;
    }
}
