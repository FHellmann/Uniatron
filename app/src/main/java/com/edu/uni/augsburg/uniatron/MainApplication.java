package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

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

        // initialize logger
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(final int priority, @Nullable final String tag) {
                return BuildConfig.DEBUG;
            }
        });

        // initialize app database
        mDataRepository = new DataRepository(AppDatabase.create(this));

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
