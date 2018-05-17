package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.edu.uni.augsburg.uniatron.model.TimeCredits;

import java.util.Collections;
import java.util.Set;

/**
 * This class handles the shared preferences.
 *
 * @author Fabio Hellmann
 */
public final class SharedPreferencesHandler {
    /** Preference for the app blacklist. */
    public static final String PREF_APP_BLACKLIST = "pref_app_blacklist";
    /** Preference for the steps per minute. */
    public static final String PREF_STEPS_PER_MINUTE = "pref_steps_per_minute";

    private final SharedPreferences mPrefs;

    /**
     * Ctr.
     *
     * @param context The application context.
     */
    public SharedPreferencesHandler(@NonNull final Context context) {
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
    public int getStepsPerMinute() {
        return mPrefs.getInt(PREF_STEPS_PER_MINUTE, TimeCredits.CREDIT_100.getStepCount());
    }
}
