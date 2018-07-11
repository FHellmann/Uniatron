package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.orhanobut.logger.Logger;

import java.util.Collections;
import java.util.LinkedHashSet;
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
    private static final String PREF_STEPS_PER_MINUTE = "pref_fitness_level";
    /**
     * Preference for the intro bonus.
     */
    private static final String PREF_INTRO_BONUS = "pref_intro_bonus";
    /**
     * Preference for the first start.
     */
    private static final String PREF_FIRST_START = "pref_first_start";

    private static final float STEP_FACTOR_EASY = 1.0f;

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
    public Set<String> getAppsBlacklist() {
        return mPrefs.getStringSet(PREF_APP_BLACKLIST, Collections.emptySet());
    }

    /**
     * Add an app to the blacklist.
     *
     * @param packageName The package name of the app.
     */
    public void addAppToBlacklist(final String packageName) {
        Logger.d("Add '" + packageName + "' to blacklist");

        final Set<String> newAppBlacklist = new LinkedHashSet<>(getAppsBlacklist());
        newAppBlacklist.add(packageName);

        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putStringSet(PREF_APP_BLACKLIST, newAppBlacklist);
        editor.apply();
    }

    /**
     * Remove an app from the blacklist.
     *
     * @param packageName The package name of the app.
     */
    public void removeAppFromBlacklist(final String packageName) {
        Logger.d("Remove '" + packageName + "' from blacklist");

        final Set<String> newAppBlacklist = new LinkedHashSet<>(getAppsBlacklist());
        newAppBlacklist.remove(packageName);

        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putStringSet(PREF_APP_BLACKLIST, newAppBlacklist);
        editor.apply();
    }

    /**
     * Get the steps amount per minute.
     *
     * @return The steps amount.
     */
    public double getStepsFactor() {
        if (mPrefs.contains(PREF_STEPS_PER_MINUTE)) {
            return Float.valueOf(
                    mPrefs.getString(
                            PREF_STEPS_PER_MINUTE,
                            String.valueOf(STEP_FACTOR_EASY)
                    )
            );
        } else {
            return STEP_FACTOR_EASY;
        }
    }

    /**
     * Register a listener for the SharedPreferences.
     *
     * @param listener the listener to register
     */
    public void registerOnPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Register a listener for the SharedPreferences.
     *
     * @param listener the listener to register
     */
    public void unregisterOnPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Checks if this is the first app launch.
     *
     * @return The value of the lookup
     */
    public boolean isFirstStart() {
        return mPrefs.getBoolean(PREF_FIRST_START, true);
    }

    /**
     * Marks the app as not first start for future launches.
     */
    public void setFirstStartDone() {
        final SharedPreferences.Editor editor = mPrefs.edit();
        //  Edit preference to make it false because we don't want this to run again
        editor.putBoolean(PREF_FIRST_START, false);
        editor.apply();
    }

    /**
     * Checks if the user is eligible for the intro bonus.
     *
     * @return true if the user is eligible to earn the intro bonus.
     */
    public boolean isIntroBonusEligible() {
        return mPrefs.getBoolean(PREF_INTRO_BONUS, true);
    }

    /**
     * Marks the intro bonus as granted.
     */
    public void setIntroBonusGranted() {
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PREF_INTRO_BONUS, false);
        editor.apply();
    }
}
