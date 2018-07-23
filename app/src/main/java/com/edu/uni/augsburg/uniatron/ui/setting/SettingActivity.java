package com.edu.uni.augsburg.uniatron.ui.setting;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.edu.uni.augsburg.uniatron.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.setting_content, new SettingFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
