package com.edu.uni.augsburg.uniatron;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.orhanobut.logger.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class handles the shared preferences.
 *
 * @author Fabio Hellmann
 */
public final class SharedPreferencesHandler implements AppPreferences {

    private static final float STEP_FACTOR_EASY = 1.0f;
    private final SharedPreferences mPrefs;
    private final Map<String, Consumer<SharedPreferences>> mListeners = new HashMap<>();

    /**
     * Ctr.
     *
     * @param context The application context.
     */
    public SharedPreferencesHandler(@NonNull final Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mPrefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> notifyDatasetChanged(key, sharedPreferences));
    }

    private void notifyDatasetChanged(@NonNull final String key, @NonNull final SharedPreferences sharedPreferences) {
        Stream.ofNullable(mListeners.entrySet())
                .filter(entry -> entry.getKey().equals(key))
                .forEach(entry -> entry.getValue().accept(sharedPreferences));
    }

    @Override
    public Set<String> getAppsBlacklist() {
        return mPrefs.getStringSet(PREF_APP_BLACKLIST, Collections.emptySet());
    }

    @Override
    public void addAppToBlacklist(@NonNull final String packageName) {
        Logger.d("Add '" + packageName + "' to blacklist");

        final Set<String> newAppBlacklist = new LinkedHashSet<>(getAppsBlacklist());
        newAppBlacklist.add(packageName);

        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putStringSet(PREF_APP_BLACKLIST, newAppBlacklist);
        editor.apply();

        notifyDatasetChanged(PREF_APP_BLACKLIST, mPrefs);
    }

    @Override
    public void removeAppFromBlacklist(@NonNull final String packageName) {
        Logger.d("Remove '" + packageName + "' from blacklist");

        final Set<String> newAppBlacklist = new LinkedHashSet<>(getAppsBlacklist());
        newAppBlacklist.remove(packageName);

        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putStringSet(PREF_APP_BLACKLIST, newAppBlacklist);
        editor.apply();

        notifyDatasetChanged(PREF_APP_BLACKLIST, mPrefs);
    }

    @Override
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

    @Override
    public void registerListener(@NonNull final String key, @NonNull final Consumer<SharedPreferences> listener) {
        mListeners.put(key, listener);
    }

    @Override
    public void removeListener(@NonNull final String key) {
        if (mListeners.containsKey(key)) {
            mListeners.remove(key);
        }
    }

    @Override
    public boolean isFirstStart() {
        return mPrefs.getBoolean(PREF_FIRST_START, true);
    }

    @Override
    public void setFirstStart(final boolean firstStart) {
        final SharedPreferences.Editor editor = mPrefs.edit();
        //  Edit preference to make it false because we don't want this to run again
        editor.putBoolean(PREF_FIRST_START, firstStart);
        editor.apply();

        notifyDatasetChanged(PREF_FIRST_START, mPrefs);
    }

    @Override
    public boolean isIntroBonusEligible() {
        return mPrefs.getBoolean(PREF_INTRO_BONUS, true);
    }

    @Override
    public void setIntroBonusGranted() {
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(PREF_INTRO_BONUS, false);
        editor.apply();

        notifyDatasetChanged(PREF_INTRO_BONUS, mPrefs);
    }
}
