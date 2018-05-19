package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import java.util.Collections;
import java.util.Set;

/**
 * This class handles the shared preferences.
 *
 * @author Fabio Hellmann
 */
public final class SharedPreferencesHandler {
    /**
     * Preference for the app blacklist.
     */
    public static final String PREF_APP_BLACKLIST = "pref_app_blacklist";
    /**
     * Preference for the steps per minute.
     */
    public static final String PREF_STEPS_PER_MINUTE = "pref_fitness_level";

    private static final double STEP_FACTOR_HARD = 2.0;
    private static final double STEP_FACTOR_MEDIUM = 1.5;
    private static final double STEP_FACTOR_EASY = 1.0;

    private final Context mContext;
    private final SharedPreferences mPrefs;

    /**
     * Ctr.
     *
     * @param context The application context.
     */
    public SharedPreferencesHandler(@NonNull final Context context) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get all the apps which have been selected to block.
     *
     * @return The list of apps.
     */
    public Set<String> getAppsToBlock() {
        return mPrefs.getStringSet(PREF_APP_BLACKLIST, Collections.emptySet());
    }

    /**
     * Get the steps amount per minute.
     *
     * @return The steps amount.
     */
    public double getStepsFactor() {
        final String levelEasy = mContext.getString(R.string.pref_fitness_level_easy);
        final String level = mPrefs.getString(PREF_STEPS_PER_MINUTE, levelEasy);
        if (level.equalsIgnoreCase(
                mContext.getString(R.string.pref_fitness_level_medium))) {
            return STEP_FACTOR_MEDIUM;
        } else if (level.equalsIgnoreCase(
                mContext.getString(R.string.pref_fitness_level_hard))) {
            return STEP_FACTOR_HARD;
        } else {
            return STEP_FACTOR_EASY;
        }
    }
}
