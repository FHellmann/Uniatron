package com.edu.uni.augsburg.uniatron.ui.setting;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

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

        final SettingViewModel model = ViewModelProviders.of(this).get(SettingViewModel.class);
        model.getInstalledApps().observe(this, data -> {
            final String[] packageNames = Stream.of(data.keySet()).toArray(String[]::new);
            final String[] labelNames = Stream.of(data.values()).toArray(String[]::new);

            final MultiSelectListPreference list =
                    (MultiSelectListPreference) findPreference(PREF_APP_BLACKLIST);
            list.setEntries(labelNames);
            list.setEntryValues(packageNames);
        });
    }
}
