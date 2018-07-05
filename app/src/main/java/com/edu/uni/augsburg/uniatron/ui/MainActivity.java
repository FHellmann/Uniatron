package com.edu.uni.augsburg.uniatron.ui;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.AppTrackingService;
import com.edu.uni.augsburg.uniatron.service.BroadcastService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;
import com.edu.uni.augsburg.uniatron.ui.day.DayFragment;
import com.edu.uni.augsburg.uniatron.ui.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The main activity is the entry point of the app.
 *
 * @author Fabio Hellmann
 */
public class MainActivity extends AppCompatActivity implements android.support.v7.widget.Toolbar.OnMenuItemClickListener {

    @BindView(R.id.fragmentPager)
    ViewPager mFragmentPager;
    @BindView(R.id.bar)
    BottomAppBar mBottomAppBar;
    @BindView(R.id.prevDateButton)
    MaterialButton mPrevDateButton;
    @BindView(R.id.dateDisplayButton)
    MaterialButton mDateDisplayButton;
    @BindView(R.id.nextDateButton)
    MaterialButton mNextDateButton;

    private MainActivityViewModel mModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mBottomAppBar.replaceMenu(R.menu.nav_bottom_bar_menu);
        mBottomAppBar.setOnMenuItemClickListener(this);

        final DayFragmentLoader adapter = new DayFragmentLoader(getSupportFragmentManager());
        mFragmentPager.setAdapter(adapter);
        mFragmentPager.setOffscreenPageLimit(1);
        mFragmentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int oldPosition;

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                // Ignore
            }

            @Override
            public void onPageSelected(int position) {
                if (position > oldPosition) {
                    // scrolled right
                    mModel.nextData();
                } else {
                    // scrolled left
                    mModel.prevData();
                }
                oldPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                // Ignore
            }
        });

        mModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mModel.getDataCountToLoad().observe(this, count -> {
            // Reset the old data by the currently loaded data
            adapter.setCount(count);
            mFragmentPager.setCurrentItem(count);
            mModel.setDate(new Date());
        });
        mModel.getCurrentDate().observe(this, date -> {
            switch (mModel.getCurrentLoadingStrategy()) {
                case Calendar.MONTH:
                    mDateDisplayButton.setText(DateUtil.formatForMonth(date.getTime()));
                    adapter.setDate(date, Calendar.MONTH);
                    break;
                case Calendar.YEAR:
                    mDateDisplayButton.setText(DateUtil.formatForYear(date.getTime()));
                    adapter.setDate(date, Calendar.YEAR);
                    break;
                case Calendar.DATE:
                default:
                    mDateDisplayButton.setText(DateUtil.formatForDate(date.getTime()));
                    adapter.setDate(date, Calendar.DATE);
                    break;
            }
            mNextDateButton.setEnabled(!mModel.isToday());
        });

        NotificationChannels.setupChannels(this);
        requestAppPermissions();
        startServices();
    }

    @OnClick(R.id.prevDateButton)
    public void onPrevClicked() {
        mFragmentPager.setCurrentItem(mFragmentPager.getCurrentItem() - 1);
        mModel.prevData();
    }

    @OnClick(R.id.dateDisplayButton)
    public void onDateDisplayClicked() {
        new DatePickerDialog(
                this,
                (datePicker, year, month, date) -> {
                    final Calendar calendar = GregorianCalendar.getInstance();
                    calendar.set(year, month, date);
                    if (DateUtil.getMinTimeOfDate(new Date()).before(calendar.getTime())) {
                        mModel.setDate(new Date());
                    } else {
                        mModel.setDate(calendar.getTime());
                    }
                },
                mModel.getCurrentDateValue(Calendar.YEAR),
                mModel.getCurrentDateValue(Calendar.MONTH),
                mModel.getCurrentDateValue(Calendar.DATE)
        ).show();
    }

    @OnClick(R.id.nextDateButton)
    public void onNextClicked() {
        mFragmentPager.setCurrentItem(mFragmentPager.getCurrentItem() + 1);
        mModel.nextData();
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

    private void startServices() {
        startService(new Intent(this, BroadcastService.class));
        startService(new Intent(this, StepCountService.class));
        startService(new Intent(this, AppTrackingService.class));
    }

    private void requestAppPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !Utils.hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkBatteryOptimized()) {
            startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkBatteryOptimized() {
        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final String packageName = getApplicationContext().getPackageName();
        return !powerManager.isIgnoringBatteryOptimizations(packageName);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.setting:
                return true;
            case R.id.group_by_day:
                menuItem.setChecked(true);
                mModel.setLoadDayCount();
                return true;
            case R.id.group_by_month:
                menuItem.setChecked(true);
                mModel.setLoadMonthCount();
                return true;
            case R.id.group_by_year:
                menuItem.setChecked(true);
                mModel.setLoadYearCount();
                return true;
            default:
                return false;
        }
    }

    private static final class DayFragmentLoader extends FragmentStatePagerAdapter {

        private int mCount;
        private Calendar mDate;
        private int mCalendarType;

        DayFragmentLoader(@NonNull final FragmentManager fm) {
            super(fm);
            mCount = 1;
            mDate = GregorianCalendar.getInstance();
        }

        void setCount(final int count) {
            mCount = count;
            notifyDataSetChanged();
        }

        void setDate(@NonNull final Calendar date, final int calendarType) {
            mDate = date;
            mCalendarType = calendarType;
        }

        @Override
        public Fragment getItem(final int position) {
            return DayFragment.createInstance(mDate, mCalendarType);
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }
}
