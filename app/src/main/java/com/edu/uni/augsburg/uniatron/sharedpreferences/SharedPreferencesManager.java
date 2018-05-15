package com.edu.uni.augsburg.uniatron.sharedpreferences;

import android.app.Activity;
import android.content.SharedPreferences;

import com.edu.uni.augsburg.uniatron.BuildConfig;

import static android.content.Context.MODE_PRIVATE;

/**
 * The SharedPreferenceManager handles various interactions with the SharedPreferences of the App.
 *
 * @author Leon Wöhrl
 */
public class SharedPreferencesManager {

    private static final String PREFS_NAME = "checkfirstlaunch";
    private static final String PREF_VERSION_CODE_KEY = "version_code";
    private static final int DOESNT_EXIST = -1;

    /**
     * checks if the activity has its first ever run.
     *
     * @param activity the activity whose first run is to be checked
     *
     * @return true if the activity had its first run
     */
    public static boolean isFirstRun(final Activity activity) {

        // Get current version code of this app
        final int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get version code saved in SharedPreferences
        final SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            return false;

        } else if (savedVersionCode == DOESNT_EXIST || currentVersionCode > savedVersionCode) {
            // this is a new install (or the user cleared the shared preferences), or upgrade
            // Update the shared preferences with the current version code
            setPreferenceInt(activity, PREF_VERSION_CODE_KEY, currentVersionCode);
            return true;
        }
        // default case is true. rather have a service restarted
        // and use resources than not starting it at all
        return true;
    }

    /**
     * writes an int into the SharedPreferences of an activity
     *
     * @param activity the activity whose SharedPreferences should be written to
     * @param preferenceName the key of the SharedPreference
     * @param value the value that is assigned to the key
     */
    private static void setPreferenceInt(final Activity activity,
                                         final String preferenceName, final int value) {
        final SharedPreferences prefs = activity.getSharedPreferences(preferenceName, MODE_PRIVATE);
        prefs.edit().putInt(preferenceName, value).apply();
    }
}
