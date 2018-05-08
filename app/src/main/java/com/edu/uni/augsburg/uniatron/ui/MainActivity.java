package com.edu.uni.augsburg.uniatron.ui;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.ui.history.HistoryFragment;
import com.edu.uni.augsburg.uniatron.ui.home.HomeFragment;
import com.edu.uni.augsburg.uniatron.ui.setting.SettingFragment;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The main activity is the entry point of the app.
 *
 * @author Fabio Hellmann
 */
public class MainActivity extends AppCompatActivity {

    private static final int NAV_POSITION_HISTORY = 0;
    private static final int NAV_POSITION_HOME = 1;
    private static final int NAV_POSITION_SETTING = 2;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        final ScreenSlidePagerAdapter mScreenSlideAdapter =
                new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mScreenSlideAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case NAV_POSITION_HISTORY:
                        mNavigation.setSelectedItemId(R.id.navigation_history);
                        break;
                    case NAV_POSITION_HOME:
                        mNavigation.setSelectedItemId(R.id.navigation_home);
                        break;
                    case NAV_POSITION_SETTING:
                        mNavigation.setSelectedItemId(R.id.navigation_settings);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(NAV_POSITION_HOME);
                    return true;
                case R.id.navigation_history:
                    mViewPager.setCurrentItem(NAV_POSITION_HISTORY);
                    return true;
                case R.id.navigation_settings:
                    mViewPager.setCurrentItem(NAV_POSITION_SETTING);
                    return true;
            }
            return false;
        });

        mNavigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == NAV_POSITION_HOME) {
            // If the user is currently looking at the home screen, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the home screen.
            mViewPager.setCurrentItem(NAV_POSITION_HOME);
        }
    }

    private final class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final Map<Integer, Fragment> mFragments;

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new LinkedHashMap<>();
            mFragments.put(NAV_POSITION_HISTORY, new HistoryFragment());
            mFragments.put(NAV_POSITION_HOME, new HomeFragment());
            mFragments.put(NAV_POSITION_SETTING, new SettingFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
