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
        // Initialize app broadcast receivers for explicit broadcasts (SDK > Android O)
        // See: https://developer.android.com/about/versions/oreo/background#broadcasts
        ExplicitBroadcastReceivers.APP_INSTALLATION.register(this);
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
