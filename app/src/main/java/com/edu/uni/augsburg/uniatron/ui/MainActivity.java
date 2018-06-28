package com.edu.uni.augsburg.uniatron.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.service.AppTrackingService;
import com.edu.uni.augsburg.uniatron.service.BroadcastService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;
import com.edu.uni.augsburg.uniatron.ui.history.HistoryFragment;
import com.edu.uni.augsburg.uniatron.ui.home.HomeFragment;
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The main activity is the entry point of the app.
 *
 * @author Fabio Hellmann
 */
public class MainActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener {

    private static final int NAV_POSITION_HOME = 0;
    private static final int NAV_POSITION_HISTORY = 1;

    @BindView(R.id.bar)
    BottomAppBar mBottomAppBar;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.textNavSteps)
    TextView mTextNavSteps;
    @BindView(R.id.textNavMinutes)
    TextView mTextNavMinutes;

    private FragmentViewChanger mFragmentViewChanger;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mBottomAppBar);

        mFragmentViewChanger = new FragmentViewChanger(getSupportFragmentManager());
        mFragmentViewChanger.setContentFragment(NAV_POSITION_HOME);
        mTabLayout.addOnTabSelectedListener(this);

        requestUsageStatsPermission();

        startServices();

        final MainActivityViewModel model = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);
        model.getRemainingAppUsageTime().observe(this,
                data -> mTextNavMinutes.setText(
                        getString(R.string.nav_text_minutes, data, data % 60)));
        model.getRemainingStepCountToday().observe(this,
                data -> mTextNavSteps.setText(getString(R.string.nav_text_steps, data)));
    }

    /**
     * Get the bottom app bar.
     *
     * @return the bottom app bar.
     */
    public BottomAppBar getBottomAppBar() {
        return mBottomAppBar;
    }

    /**
     * Is called when the fab is clicked.
     */
    @OnClick(R.id.fab)
    public void onFabClicked() {
        final Intent nextIntent = new Intent(this, TimeCreditShopActivity.class);
        TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(nextIntent)
                .startActivities();
    }

    @Override
    public void onBackPressed() {
        if (mTabLayout.getSelectedTabPosition() == NAV_POSITION_HOME) {
            // If the user is currently looking at the home screen,
            // allow the system to handle the Back button. This
            // calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the home screen.
            mFragmentViewChanger.setContentFragment(NAV_POSITION_HOME);
        }
    }

    private void startServices() {
        startService(new Intent(this, BroadcastService.class));
        startService(new Intent(this, StepCountService.class));
        startService(new Intent(this, AppTrackingService.class));
    }

    private void requestUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !Utils.hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @Override
    public void onTabSelected(final TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case NAV_POSITION_HOME:
                mFragmentViewChanger.setContentFragment(NAV_POSITION_HOME);
                break;
            case NAV_POSITION_HISTORY:
                mFragmentViewChanger.setContentFragment(NAV_POSITION_HISTORY);
                break;
        }
    }

    @Override
    public void onTabUnselected(final TabLayout.Tab tab) {
        // not relevant
    }

    @Override
    public void onTabReselected(final TabLayout.Tab tab) {
        // not relevant
    }

    private static final class FragmentViewChanger {
        @NonNull
        private final FragmentManager mFragmentManager;
        private final Map<Integer, Fragment> mFragmentMap;

        private FragmentViewChanger(@NonNull final FragmentManager fragmentManager) {
            mFragmentManager = fragmentManager;
            mFragmentMap = new LinkedHashMap<>();
            mFragmentMap.put(NAV_POSITION_HOME, new HomeFragment());
            mFragmentMap.put(NAV_POSITION_HISTORY, new HistoryFragment());
        }

        private void setContentFragment(final int position) {
            final FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(android.R.id.content, mFragmentMap.get(position));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
