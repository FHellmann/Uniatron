package com.edu.uni.augsburg.uniatron.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.service.AppTrackingService;
import com.edu.uni.augsburg.uniatron.service.BroadcastService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;
import com.edu.uni.augsburg.uniatron.ui.history.HistoryFragment;
import com.edu.uni.augsburg.uniatron.ui.home.HomeFragment;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingFragment;
import com.rvalerio.fgchecker.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The main activity is the entry point of the app.
 *
 * @author Fabio Hellmann
 */
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int NAV_POSITION_HISTORY = 0;
    private static final int NAV_POSITION_HOME = 1;
    private static final int NAV_POSITION_SETTING = 2;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        final ScreenSlidePagerAdapter mScreenSlideAdapter =
                new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mScreenSlideAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(mScreenSlideAdapter.mFragments.size());
        mViewPager.setPageTransformer(true, new CrossFadePageTransformer());

        mNavigation.setOnNavigationItemSelectedListener(this);
        mNavigation.setSelectedItemId(R.id.navigation_home);

        requestUsageStatsPermission();

        startServices();
    }

    @Override
    public void onPageScrolled(final int position,
                               final float positionOffset,
                               final int positionOffsetPixels) {
        // can be ignored
    }

    @Override
    public void onPageSelected(final int position) {
        switch (position) {
            case NAV_POSITION_HISTORY:
                mNavigation.setSelectedItemId(R.id.navigation_history);
                break;
            case NAV_POSITION_SETTING:
                mNavigation.setSelectedItemId(R.id.navigation_settings);
                break;
            case NAV_POSITION_HOME:
            default:
                mNavigation.setSelectedItemId(R.id.navigation_home);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
        // can be ignored
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == NAV_POSITION_HOME) {
            // If the user is currently looking at the home screen,
            // allow the system to handle the Back button. This
            // calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the home screen.
            mViewPager.setCurrentItem(NAV_POSITION_HOME, false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                mViewPager.setCurrentItem(NAV_POSITION_HOME, false);
                return true;
            case R.id.navigation_history:
                mViewPager.setCurrentItem(NAV_POSITION_HISTORY, false);
                return true;
            case R.id.navigation_settings:
                mViewPager.setCurrentItem(NAV_POSITION_SETTING, false);
                return true;
            default:
                return false;
        }
    }

    private void startServices() {
        startService(new Intent(getBaseContext(), BroadcastService.class));
        startService(new Intent(getBaseContext(), StepCountService.class));
        startService(new Intent(getBaseContext(), AppTrackingService.class));
    }

    private void requestUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !Utils.hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private static final class CrossFadePageTransformer implements ViewPager.PageTransformer {
        private static final float ZERO = 0.0f;
        private static final float MINUS_ONE = -1.0f;
        private static final float PLUS_ONE = 1.0f;

        @Override
        public void transformPage(@NonNull final View view, final float position) {
            if (position <= MINUS_ONE || position >= PLUS_ONE) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(ZERO);
            } else if (position == ZERO) {
                view.setTranslationX(view.getWidth() * position);
                view.setAlpha(PLUS_ONE);
            } else {
                // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setTranslationX(view.getWidth() * -position);
                view.setAlpha(PLUS_ONE - Math.abs(position));
            }
        }
    }

    static final class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final Map<Integer, Fragment> mFragments;

        ScreenSlidePagerAdapter(@NonNull final FragmentManager fragmentManager) {
            super(fragmentManager);
            mFragments = new LinkedHashMap<>();
            mFragments.put(NAV_POSITION_HISTORY, new HistoryFragment());
            mFragments.put(NAV_POSITION_HOME, new HomeFragment());
            mFragments.put(NAV_POSITION_SETTING, new SettingFragment());
        }

        @Override
        public Fragment getItem(final int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
