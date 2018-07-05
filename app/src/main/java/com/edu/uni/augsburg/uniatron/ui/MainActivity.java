package com.edu.uni.augsburg.uniatron.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.AppTrackingService;
import com.edu.uni.augsburg.uniatron.service.BroadcastService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;
import com.edu.uni.augsburg.uniatron.ui.history.HistoryFragment;
import com.edu.uni.augsburg.uniatron.ui.home.HomeFragment;
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The main activity is the entry point of the app.
 *
 * @author Fabio Hellmann
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bar)
    BottomAppBar mBottomAppBar;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;
    @BindView(R.id.textNavSteps)
    TextView mTextNavMoney;
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
        mFragmentViewChanger.selectHome();
        mBottomNavigation.setOnNavigationItemSelectedListener(this);

        final MainActivityViewModel model = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);
        model.getRemainingAppUsageTime().observe(this,
                data -> mTextNavMinutes.setText(
                        getString(R.string.nav_text_minutes, data / 60, data % 60)));
        model.getRemainingStepCountToday().observe(this,
                data -> mTextNavMoney.setText(getString(R.string.nav_text_money, data)));

        requestUsageStatsPermission();
        requestBatteryOptimizationDisablePermission();
        NotificationChannels.setupChannels(this);
        startServices();
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
        if (mBottomNavigation.getSelectedItemId() == R.id.navigation_home) {
            // If the user is currently looking at the home screen,
            // allow the system to handle the Back button. This
            // calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the home screen.
            mBottomNavigation.setSelectedItemId(R.id.navigation_home);
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

    private void requestBatteryOptimizationDisablePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkBatteryOptimized()) {
            startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        }
    }

    private boolean checkBatteryOptimized() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return !powerManager
                    .isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (R.id.navigation_home == menuItem.getItemId()) {
            mFragmentViewChanger.selectHome();
            return true;
        } else if (R.id.navigation_history == menuItem.getItemId()) {
            mFragmentViewChanger.selectHistory();
            return true;
        }
        return false;
    }

    private static final class FragmentViewChanger {
        @NonNull
        private final FragmentManager mFragmentManager;
        private final HomeFragment mHomeFragment;
        private final HistoryFragment mHistoryFragment;

        FragmentViewChanger(@NonNull final FragmentManager fragmentManager) {
            mFragmentManager = fragmentManager;
            mHomeFragment = new HomeFragment();
            mHistoryFragment = new HistoryFragment();
        }

        private void selectHome() {
            setContentFragment(mHomeFragment);
        }

        private void selectHistory() {
            setContentFragment(mHistoryFragment);
        }

        private void setContentFragment(final Fragment fragment) {
            final FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.content_fragment, fragment);
            transaction.disallowAddToBackStack();
            transaction.commit();
        }
    }
}
