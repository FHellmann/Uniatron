package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.EmotionDao;
import com.edu.uni.augsburg.uniatron.domain.dao.StepCountDao;
import com.edu.uni.augsburg.uniatron.domain.dao.SummaryDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;

/**
 * The app context provides the interfaces to
 * the preferences and backend.
 *
 * @author Fabio Hellmann
 */
public interface AppContext {

    /**
     * Get an instance of the main application.
     *
     * @param context The context.
     * @return The main application.
     */
    static AppContext getInstance(@NonNull final Context context) {
        return (MainApplication) context.getApplicationContext();
    }

    /**
     * Get the SharedPreferencesHandler.
     *
     * @return The SharedPreferencesHandler.
     */
    AppPreferences getPreferences();

    /**
     * Get the app usage dao.
     *
     * @return The app usage dao.
     */
    AppUsageDao getAppUsageDao();

    /**
     * Get the emotion dao.
     *
     * @return The emotion dao.
     */
    EmotionDao getEmotionDao();

    /**
     * Get the step count dao.
     *
     * @return The step count dao.
     */
    StepCountDao getStepCountDao();

    /**
     * Get the summary dao.
     *
     * @return The summary dao.
     */
    SummaryDao getSummaryDao();

    /**
     * Get the time credit dao.
     *
     * @return The time credit dao.
     */
    TimeCreditDao getTimeCreditDao();
}
