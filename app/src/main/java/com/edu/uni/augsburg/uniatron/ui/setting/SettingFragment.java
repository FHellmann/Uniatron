package com.edu.uni.augsburg.uniatron.ui.setting;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;

import static com.edu.uni.augsburg.uniatron.SharedPreferencesHandler.PREF_APP_BLACKLIST;

/**
 * This fragment is for user specific configuration.
 *
 * @author Fabio Hellmann
 */
public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        final MultiSelectListPreference list =
                (MultiSelectListPreference) findPreference(PREF_APP_BLACKLIST);
        list.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(getClass().getSimpleName(), "Pref-List: " + newValue);
            return true;
        });

        final SettingViewModel model = ViewModelProviders.of(this).get(SettingViewModel.class);
        model.getInstalledApps().observe(this, data -> {
            final String[] entries = Stream.of(data).toArray(String[]::new);
            list.setEntries(entries);
            list.setEntryValues(entries);
        });
    }
}
