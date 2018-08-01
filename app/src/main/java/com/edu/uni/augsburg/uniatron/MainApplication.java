package com.edu.uni.augsburg.uniatron;

import android.app.Application;
import android.content.Context;
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

    /**
     * Get the SharedPreferencesHandler.
     *
     * @param context The current context of the callee.
     * @return The SharedPreferencesHandler.
     */
    public static SharedPreferencesHandler getSharedPreferencesHandler(final Context context) {
        return ((MainApplication) context.getApplicationContext()).mSharedPreferencesHandler;
    }

    /**
     * Get the app usage dao.
     *
     * @param context The context.
     * @return The app usage dao.
     */
    public static AppUsageDao getAppUsageDao(final Context context) {
        return AppUsageDao.create(((MainApplication) context.getApplicationContext()).mQueryProvider);
    }

    /**
     * Get the emotion dao.
     *
     * @param context The context.
     * @return The emotion dao.
     */
    public static EmotionDao getEmotionDao(final Context context) {
        return EmotionDao.create(((MainApplication) context.getApplicationContext()).mQueryProvider);
    }

    /**
     * Get the step count dao.
     *
     * @param context The context.
     * @return The step count dao.
     */
    public static StepCountDao getStepCountDao(final Context context) {
        return StepCountDao.create(((MainApplication) context.getApplicationContext()).mQueryProvider);
    }

    /**
     * Get the summary dao.
     *
     * @param context The context.
     * @return The summary dao.
     */
    public static SummaryDao getSummaryDao(final Context context) {
        return SummaryDao.create(((MainApplication) context.getApplicationContext()).mQueryProvider);
    }

    /**
     * Get the time credit dao.
     *
     * @param context The context.
     * @return The time credit dao.
     */
    public static TimeCreditDao getTimeCreditDao(final Context context) {
        return TimeCreditDao.create(((MainApplication) context.getApplicationContext()).mQueryProvider);
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
