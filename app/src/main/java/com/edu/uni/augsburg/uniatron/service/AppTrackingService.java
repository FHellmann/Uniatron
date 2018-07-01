package com.edu.uni.augsburg.uniatron.service;

import android.app.Notification;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.notification.builder.AidFinishNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.builder.TimeUpNotificationBuilder;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.AppChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This service detects when the screen is activated/deactivated
 * and tracks the time of the currently opened application.
 * If there is no remaining app-usage-time, the MainActivity comes to foreground
 *
 * @author Danilo Hoss
 */
public class AppTrackingService extends LifecycleService {

    private static final int DELAY_IN_MILLISECONDS = 1000;
    private static final List<String> FILTERS = new ArrayList<>();
    private static final int NOTIFICATION_ONE_MINUTE = 59;
    private static final int NOTIFICATION_FIVE_MINUTES = 299;
    private static final int NOTIFICATION_TEN_MINUTES = 599;
    private Set<String> blackList;
    private final AppChecker mAppChecker = new AppChecker();
    private final List<Set<String>> allBlackLists = new ArrayList<>();
    private Set<String> mDBBlacklist;
    private SharedPreferencesHandler mSharedPreferencesHandler;
    private DataRepository mRepository;

    private final OnSharedPreferenceChangeListener mSharedPrefsListener
            = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
            Log.d(getClass().toString(), "shared prefs changed");
            Log.d(getClass().toString(), "before" + mDBBlacklist.toString());

            // remove observers of old blacklist
            // TODO this doesn't work
            //update blacklist
            mDBBlacklist = mSharedPreferencesHandler.getAppsBlacklist();
            allBlackLists.add(mDBBlacklist);
            removeAllObserver();
            Log.d(getClass().toString(), "after" + mDBBlacklist.toString());

            // observe the new one
            mRepository.getRemainingAppUsageTimeToday(mDBBlacklist)
                    .observe(AppTrackingService.this, usageTimeObserver);
            // TODO we will now have multiple observers reporting different values (for different blacklists)
        }
    };

    private Boolean commitStatus = false;
    private Boolean lastCommitStatus = false;
    private Long mLearningAidDiffMillis;
    private final Observer<Long> learningAidObserver = new Observer<Long>() {
        @Override
        public void onChanged(@Nullable final Long learningAidDiff) {
            if (learningAidDiff == null) {
                mLearningAidDiffMillis = 1L;
            } else {
                Log.d(getClass().toString(), "learningaiddiff onchanged: " + learningAidDiff);
                mLearningAidDiffMillis = learningAidDiff;
            }
        }
    };
    // initialize with a value > 0
    // in case we use the value before the observer reports it
    private int remainingUsageTime = 10;
    private final Observer<Integer> usageTimeObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable final Integer remainingUsageTimeDB) {
            if (remainingUsageTimeDB == null) {
                remainingUsageTime = 10;
            } else {
                remainingUsageTime = remainingUsageTimeDB;
                Log.d(getClass().toString(), "observer = " + remainingUsageTime);
            }
        }
    };


    private final void removeAllObserver(){
        for (Set<String> elem: allBlackLists
             ) {
            mRepository.getRemainingAppUsageTimeToday(blackList).removeObserver(AppTrackingService.this.usageTimeObserver);
        }
    }

    private final BroadcastReceiver mScreenEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                lastCommitStatus = commitStatus;
                commitStatus = false;
                stopAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                commitStatus = lastCommitStatus;
                startAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        FILTERS.add(getDefaultLauncherPackageName());
        FILTERS.add("com.edu.uni.augsburg.uniatron");

        //register Events ScreenOn and ScreenOff
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenEventReceiver, filter);

        mSharedPreferencesHandler = MainApplication.getSharedPreferencesHandler(getBaseContext());
        mRepository = MainApplication.getRepository(getBaseContext());

        mDBBlacklist = mSharedPreferencesHandler.getAppsBlacklist();
        allBlackLists.add(mDBBlacklist);

        mRepository.getRemainingAppUsageTimeToday(mDBBlacklist)
                .observe(this, usageTimeObserver);

        mSharedPreferencesHandler.registerOnPreferenceChangeListener(mSharedPrefsListener);

        mRepository.getLatestLearningAidDiff().observe(this, learningAidObserver);


        startAppChecker();
    }

    /**
     * Executes the start of the service.
     *
     * @param intent  the intent
     * @param flags   the flags
     * @param startId the startid
     * @return the way the service is started
     */
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mScreenEventReceiver);

        mRepository.getRemainingAppUsageTimeToday(mDBBlacklist)
                .removeObserver(usageTimeObserver);
        mRepository.getLatestLearningAidDiff().removeObserver(learningAidObserver);

        mSharedPreferencesHandler.unRegisterOnPreferenceChangeListener(mSharedPrefsListener);

        super.onDestroy();
    }

    // called periodically and handles all app blocking logic
    private void delegateAppUsage(final String appName, final int timeMillis) {

        // learning aid blocking has higher priority
        if (!blockByLearningAid(appName)) {
            blockByTimeCreditIfTimeUp(appName);
            showNotificationIfTimeAlmostUp();
        }

        if (commitStatus || !mDBBlacklist.contains(appName)) {
            commitAppUsageTime(appName, timeMillis);
        }
    }

    private void showNotificationIfTimeAlmostUp() {
        if (remainingUsageTime == NOTIFICATION_ONE_MINUTE
                || remainingUsageTime == NOTIFICATION_FIVE_MINUTES
                || remainingUsageTime == NOTIFICATION_TEN_MINUTES) {
            final Context context = getApplicationContext();
            final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(context, remainingUsageTime + 1);
            final Notification notification = builder.build();
            final int notificationId = builder.getId();
            NotificationManagerCompat.from(context).notify(notificationId, notification);
        }
    }


    private void blockByTimeCreditIfTimeUp(final String appName) {
        if (remainingUsageTime <= 0 && mDBBlacklist.contains(appName)) {
            commitStatus = false;

            Log.d(getClass().toString(), "blocking app. time remaining = " + remainingUsageTime);
            final Intent blockIntent = new Intent(getApplicationContext(), MainActivity.class);
            blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(blockIntent);
        } else {
            commitStatus = true;
        }
    }

    private boolean blockByLearningAid(final String appName) {

        mRepository.getLatestLearningAidDiff().removeObserver(learningAidObserver);
        mRepository.getLatestLearningAidDiff().observe(this, learningAidObserver);

        final long timeBlockedMinutes = TimeCredits.CREDIT_LEARNING.getBlockedMinutes();
        final long timePassedMinutes = TimeUnit.MINUTES.convert(mLearningAidDiffMillis, TimeUnit.MILLISECONDS);

        if (mLearningAidDiffMillis > 0 && timePassedMinutes < timeBlockedMinutes
                && mDBBlacklist.contains(appName)) {

            Log.d(getClass().toString(), "aid active. blocking.. "
                    + " timeblocked: " + timeBlockedMinutes
                    + " timepassed: " + timePassedMinutes);
            Log.d(getClass().toString(), "mLearningAidDiffMillis: " + mLearningAidDiffMillis);
            commitStatus = false;

            if (mLearningAidDiffMillis == TimeCredits.CREDIT_LEARNING.getBlockedMinutes() * 60 * 1000 - 1) {
                final Context context = getApplicationContext();
                final AidFinishNotificationBuilder builder = new AidFinishNotificationBuilder(context);
                final Notification notification = builder.build();
                final int notificationId = builder.getId();
                NotificationManagerCompat.from(context).notify(notificationId, notification);
            }

            final Intent blockIntent = new Intent(getApplicationContext(), TimeCreditShopActivity.class);
            blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(blockIntent);
            return true;
        } else {
            commitStatus = true;
            return false;
        }
    }

    private void commitAppUsageTime(final String appName, final int timeMillis) {
        if (!TextUtils.isEmpty(appName) && !FILTERS.contains(appName)) {
            final int time = (int) TimeUnit.SECONDS.convert(timeMillis, TimeUnit.MILLISECONDS);
            mRepository.addAppUsage(appName, time);
        }
    }

    private String getDefaultLauncherPackageName() {
        final PackageManager localPackageManager = getPackageManager();
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .activityInfo.packageName;
    }

    private void startAppChecker() {
        mAppChecker.whenAny(process -> delegateAppUsage(process, DELAY_IN_MILLISECONDS))
                .timeout(DELAY_IN_MILLISECONDS)
                .start(getBaseContext());
    }

    private void stopAppChecker() {
        mAppChecker.stop();
    }
}
