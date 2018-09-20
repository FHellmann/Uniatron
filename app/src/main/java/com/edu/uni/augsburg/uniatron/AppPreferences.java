package com.edu.uni.augsburg.uniatron;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.annimon.stream.function.Consumer;

import java.util.Set;

/**
 * The interface for preferences calls.
 *
 * @author Fabio Hellmann
 */
public interface AppPreferences {

    /**
     * Preference for the app blacklist.
     */
    String PREF_APP_BLACKLIST = "pref_app_blacklist";
    /**
     * Preference for the steps per minute.
     */
    String PREF_STEPS_PER_MINUTE = "pref_fitness_level";
    /**
     * Preference for the intro bonus.
     */
    String PREF_INTRO_BONUS = "pref_intro_bonus";
    /**
     * Preference for the first start.
     */
    String PREF_FIRST_START = "pref_first_start";

    /**
     * Get all the apps which have been selected to block.
     *
     * @return The list of apps.
     */
    Set<String> getAppsBlacklist();

    /**
     * Add an app to the blacklist.
     *
     * @param packageName The package name of the app.
     */
    void addAppToBlacklist(@NonNull String packageName);

    /**
     * Remove an app from the blacklist.
     *
     * @param packageName The package name of the app.
     */
    void removeAppFromBlacklist(@NonNull String packageName);

    /**
     * Get the steps amount per minute.
     *
     * @return The steps amount.
     */
    double getStepsFactor();

    /**
     * Register a listener for the SharedPreferences.
     *
     * @param key      The key of the preference.
     * @param listener The listener to register
     */
    void registerListener(@NonNull String key, @NonNull Consumer<SharedPreferences> listener);

    /**
     * Register a listener for the SharedPreferences.
     *
     * @param key The key of the preference.
     */
    void removeListener(@NonNull String key);

    /**
     * Checks if this is the first app launch.
     *
     * @return The value of the lookup
     */
    boolean isFirstStart();

    /**
     * Marks the app as not first start for future launches.
     * @param firstStart Sets if it is the first start or not.
     */
    void setFirstStart(boolean firstStart);

    /**
     * Checks if the user is eligible for the intro bonus.
     *
     * @return true if the user is eligible to earn the intro bonus.
     */
    boolean isIntroBonusEligible();

    /**
     * Marks the intro bonus as granted.
     */
    void setIntroBonusGranted();
}
