package com.edu.uni.augsburg.uniatron.ui.setting;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;

import static com.edu.uni.augsburg.uniatron.SharedPreferencesHandler.PREF_APP_BLACKLIST;

/**
 * The preference fragment for the general part.
 *
 * @author Fabio Hellmann
 */
public class SettingFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(final Bundle bundle, final String root) {
        setPreferencesFromResource(R.xml.preferences, root);

        final SettingViewModel mModel = ViewModelProviders.of(this).get(SettingViewModel.class);

        mModel.getInstalledApps().observe(this, data -> {
            final String[] packageNames = Stream.of(data.keySet()).toArray(String[]::new);
            final String[] labelNames = Stream.of(data.values()).toArray(String[]::new);

            final MultiSelectListPreference list =
                    (MultiSelectListPreference) findPreference(PREF_APP_BLACKLIST);
            list.setEntries(labelNames);
            list.setEntryValues(packageNames);
        });
    }
}
