package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DatabaseUtil;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * The application context for this app.
 *
 * @author Fabio Hellmann
 */
public class MainApplication extends Application {
    private DataRepository mDataRepository;
    private SharedPreferencesHandler mSharedPreferencesHandler;

    /**
     * Get the data repository.
     *
     * @param context The current context of the callee.
     * @return The data repository.
     */
    public static DataRepository getRepository(final Context context) {
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

        final AppDatabase database = Room.inMemoryDatabaseBuilder(this, AppDatabase.class).build();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseUtil.createRandomData(database);
                return null;
            }
        }.execute();

        // initialize app database
        mDataRepository = new DataRepository(database); //AppDatabase.create(this));
        mSharedPreferencesHandler = new SharedPreferencesHandler(this);
        NotificationChannels.setupChannels(this);
    }
}
