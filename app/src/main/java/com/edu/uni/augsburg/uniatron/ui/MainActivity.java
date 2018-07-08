package com.edu.uni.augsburg.uniatron.ui;

import android.app.AppOpsManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.AppTrackingService;
import com.edu.uni.augsburg.uniatron.service.BroadcastService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;
import com.edu.uni.augsburg.uniatron.ui.history.HistoryFragment;
import com.edu.uni.augsburg.uniatron.ui.home.HomeFragment;
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;
import com.edu.uni.augsburg.uniatron.ui.onboarding.OnboardingActivity;

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
        mFragmentViewChanger.selectHome();
        mTabLayout.addOnTabSelectedListener(this);

        final MainActivityViewModel model = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);
        model.getRemainingAppUsageTime().observe(this,
                data -> mTextNavMinutes.setText(
                        getString(R.string.nav_text_minutes, data / 60, data % 60)));
        model.getRemainingStepCountToday().observe(this,
                data -> mTextNavSteps.setText(getString(R.string.nav_text_steps, data)));


        startOnboarding();

        NotificationChannels.setupChannels(this);
        startServices();
    }

    private void startOnboarding() {
        final SharedPreferencesHandler sharedPrefsHandler =
                MainApplication.getSharedPreferencesHandler(getApplicationContext());

        // relaunch onboarding if permission request was ignored
        if (sharedPrefsHandler.isFirstStart()
                || needUsageAccessPermission()
                || needBatteryWhitelistPermission()) {
            //  Launch onboarding
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
                }
            });
            sharedPrefsHandler.setFirstStart();
        }
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
            setNavToHome();
        }
    }

    private void startServices() {
        startService(new Intent(this, BroadcastService.class));
        startService(new Intent(this, StepCountService.class));
        startService(new Intent(this, AppTrackingService.class));
    }

    private boolean needUsageAccessPermission() {
        ApplicationInfo applicationInfo;
        PackageManager packageManager;
        AppOpsManager appOpsManager;
        int mode = 0;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getApplicationContext().getPackageName(), 0);
            appOpsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
            if (appOpsManager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // no way to find out. assume we need permission
                return true;
            } else {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }

            return mode != AppOpsManager.MODE_ALLOWED;

        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    private boolean needBatteryWhitelistPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return !powerManager
                    .isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        return true;
    }

    private void setNavToHome() {
        final TabLayout.Tab tab = mTabLayout.getTabAt(NAV_POSITION_HOME);
        if (tab != null) {
            tab.select();
        }
    }

    @Override
    public void onTabSelected(final TabLayout.Tab tab) {
        if (NAV_POSITION_HOME == tab.getPosition()) {
            mFragmentViewChanger.selectHome();
        } else if (NAV_POSITION_HISTORY == tab.getPosition()) {
            mFragmentViewChanger.selectHistory();
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
