package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;
import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.EmotionDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.SummaryDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * The application context for this app.
 *
 * @author Fabio Hellmann
 */
public class MainApplication extends Application {
    private SharedPreferencesHandler mSharedPreferencesHandler;
    private QueryProvider mQueryProvider;

    public static MainApplication getInstance(@NonNull final Context context) {
        return (MainApplication) context.getApplicationContext();
    }

    /**
     * Get the SharedPreferencesHandler.
     *
     * @return The SharedPreferencesHandler.
     */
    public SharedPreferencesHandler getSharedPreferencesHandler() {
        return mSharedPreferencesHandler;
    }

    /**
     * Get the app usage dao.
     *
     * @return The app usage dao.
     */
    public AppUsageDao getAppUsageDao() {
        return AppUsageDao.create(mQueryProvider);
    }

    /**
     * Get the emotion dao.
     *
     * @return The emotion dao.
     */
    public EmotionDao getEmotionDao() {
        return EmotionDao.create(mQueryProvider);
    }

    /**
     * Get the step count dao.
     *
     * @return The step count dao.
     */
    public StepCountDao getStepCountDao() {
        return StepCountDao.create(mQueryProvider);
    }

    /**
     * Get the summary dao.
     *
     * @return The summary dao.
     */
    public SummaryDao getSummaryDao() {
        return SummaryDao.create(mQueryProvider);
    }

    /**
     * Get the time credit dao.
     *
     * @return The time credit dao.
     */
    public TimeCreditDao getTimeCreditDao() {
        return TimeCreditDao.create(mQueryProvider);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(final int priority, @Nullable final String tag) {
                return BuildConfig.DEBUG;
            }
        });
        mQueryProvider = QueryProvider.create(this);
        mSharedPreferencesHandler = new SharedPreferencesHandler(this);
        NotificationChannels.setupChannels(this);
    }
}
