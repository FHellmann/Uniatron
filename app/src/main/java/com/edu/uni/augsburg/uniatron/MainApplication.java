package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.DataSource;
import com.edu.uni.augsburg.uniatron.domain.DatabaseSource;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * The application context for this app.
 *
 * @author Fabio Hellmann
 */
public class MainApplication extends Application {
    private DataSource mDataRepository;
    private SharedPreferencesHandler mSharedPreferencesHandler;

    /**
     * Get the data repository.
     *
     * @param context The current context of the callee.
     * @return The data repository.
     */
    public static DataSource getDataSource(final Context context) {
        return ((MainApplication) context.getApplicationContext()).mDataRepository;
    }

    /**
     * Get the SharedPreferencesHandler.
     *
     * @param context The current context of the callee.
     * @return The SharedPreferencesHandler.
     */
    public static SharedPreferencesHandler getSharedPreferencesHandler(final Context context) {
        return ((MainApplication) context.getApplicationContext()).mSharedPreferencesHandler;
    }

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
        mDataRepository = new DataRepository(DatabaseSource.create(this));
        mSharedPreferencesHandler = new SharedPreferencesHandler(this);
        NotificationChannels.setupChannels(this);
    }
}
