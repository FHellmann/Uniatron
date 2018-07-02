package com.edu.uni.augsburg.uniatron.ui.setting;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.MultiSelectListPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.edu.uni.augsburg.uniatron.SharedPreferencesHandler.PREF_APP_BLACKLIST;

/**
 * This fragment is for user specific configuration.
 *
 * @author Fabio Hellmann
 */
public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setOnMenuItemClickListener(menuItem -> {
            if (android.R.id.home == menuItem.getItemId()) {
                finish();
                return true;
            }
            return false;
        });

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.setting_content, new GeneralPreferences())
                .commit();
    }

    /**
     * The preference fragment for the general part.
     *
     * @author Fabio Hellmann
     */
    public static class GeneralPreferences extends PreferenceFragmentCompat {
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
}
