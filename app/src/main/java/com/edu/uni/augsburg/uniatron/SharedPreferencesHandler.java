package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.content.SharedPreferences;
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
    public static final String PREF_STEPS_PER_MINUTE = "pref_fitness_level";

    private static final float STEP_FACTOR_EASY = 1.0f;

    private static SharedPreferencesHandler mInstance;
    private final SharedPreferences mPrefs;
    private OnBlacklistChangeListener mOnBlacklistChangeListener;

    /**
     * Ctr.
     *
     * @param context The application context.
     */
    private SharedPreferencesHandler(@NonNull final Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Get a single instance of the shared preferences handler.
     *
     * @param context The context.
     * @return The shared preferences handler.
     */
    public static SharedPreferencesHandler getInstance(@NonNull final Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesHandler(context);
        }
        return mInstance;
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
     * Add a app to the blacklist.
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

        if (mOnBlacklistChangeListener != null) {
            mOnBlacklistChangeListener.onChanged(packageName, true);
        }
    }

    /**
     * Remove a app from the blacklist.
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

        if (mOnBlacklistChangeListener != null) {
            mOnBlacklistChangeListener.onChanged(packageName, false);
        }
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
     * Get the on blacklist change listener.
     *
     * @return The listener.
     */
    public OnBlacklistChangeListener getOnBlacklistChangeListener() {
        return mOnBlacklistChangeListener;
    }

    /**
     * Set the on blacklist change listener.
     *
     * @param onBlacklistChangeListener The listener.
     */
    public void setOnBlacklistChangeListener(
            final OnBlacklistChangeListener onBlacklistChangeListener) {
        this.mOnBlacklistChangeListener = onBlacklistChangeListener;
    }

    /**
     * The listener for on blacklist change events.
     *
     * @author Fabio Hellmann
     */
    public interface OnBlacklistChangeListener {
        /**
         * Will be called when the blacklist is changed by the amount of entries.
         *
         * @param packageName The package name.
         * @param added       {@code true} if the package was installed, {@code false} otherwise
         */
        void onChanged(final String packageName, final boolean added);
    }
}
