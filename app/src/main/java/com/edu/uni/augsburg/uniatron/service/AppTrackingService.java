package com.edu.uni.augsburg.uniatron.service;

import android.app.Notification;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.notification.builder.TimeUpNotificationBuilder;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;
import com.rvalerio.fgchecker.AppChecker;

import java.util.ArrayList;
import java.util.List;
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
    private static final int NOTIFICATION_TIME_OFFSET = 1;
    private static final int ONE_MINUTE = 60;
    private static final int FIVE_MINUTES = 300;
    private static final int TEN_MINUTES = 600;

    private final AppChecker mAppChecker = new AppChecker();
    private SharedPreferencesHandler mSharedPreferencesHandler;
    private DataRepository mRepository;

    private Boolean commitStatus = false;
    private Boolean lastCommitStatus = false;

    private Long timePassedLearningAid;
    private final Observer<Long> learningAidObserver = new Observer<Long>() {
        @Override
        public void onChanged(@Nullable final Long learningAidDiff) {
            if (learningAidDiff == null) {
                timePassedLearningAid = 0L;
            } else {
                timePassedLearningAid = learningAidDiff;
            }
        }
    };
    // initialize with a value > 0
    // in case we use the value before the observer reports it
    private int remainingUsageTime = 10;
    private final Observer<Integer> usageTimeObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer remainingUsageTimeDB) {
            if (remainingUsageTimeDB == null) {
                remainingUsageTime = 10;
            } else {
                remainingUsageTime = remainingUsageTimeDB;
                Log.d(getClass().toString(), "observer = " + remainingUsageTime);
            }
        }
    };

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

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenEventReceiver, filter);

        mSharedPreferencesHandler = MainApplication.getSharedPreferencesHandler(getBaseContext());

        mSharedPreferencesHandler.registerOnPreferenceChangeListener((sharedPreferences, s) -> {
            mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                    .removeObserver(usageTimeObserver);
            mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                    .observe(AppTrackingService.this, usageTimeObserver);
        });

        mRepository = MainApplication.getRepository(getBaseContext());

        mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                .observe(this, usageTimeObserver);
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

        mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                .removeObserver(usageTimeObserver);
        mRepository.getLatestLearningAidDiff().removeObserver(learningAidObserver);

        super.onDestroy();
    }

    // is being called periodically and handles all logic
    private void delegateAppUsage(final String appName, final int timeMillis) {

        if (!blockByLearningAidIfNecessary(appName)){
            blockAppIfNecessary(appName);
        }
        showNotificationIfNecessary();

        if (commitStatus || !mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            commitAppUsageTime(appName, timeMillis);
        }
    }

    private void showNotificationIfNecessary() {
        if (remainingUsageTime + 1 == ONE_MINUTE
                || remainingUsageTime == FIVE_MINUTES - NOTIFICATION_TIME_OFFSET
                || remainingUsageTime == TEN_MINUTES - NOTIFICATION_TIME_OFFSET) {
            final Context context = getApplicationContext();
            final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(context, remainingUsageTime + 1);
            final Notification notification = builder.build();
            final int notificationId = builder.getId();
            NotificationManagerCompat.from(context).notify(notificationId, notification);
        }
    }

    private boolean blockAppIfNecessary(final String appName) {
        if (remainingUsageTime <= 0 && mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            commitStatus = false;

            Log.d(getClass().toString(), "bloacking app. time remaining = " + remainingUsageTime);
            final Intent blockIntent = new Intent(getApplicationContext(), MainActivity.class);
            blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(blockIntent);
            return true;
        } else {
            commitStatus = true;
            return false;
        }
    }

    private boolean blockByLearningAidIfNecessary(final String appName) {
        final long timeLeft = TimeCredits.CREDIT_LEARNING.getBlockedMinutes()
                - TimeUnit.MINUTES.convert(timePassedLearningAid, TimeUnit.MILLISECONDS);
        //Log.d(getClass().toString(), "left: " + timeLeft + " blocked: " + TimeCredits.CREDIT_LEARNING.getBlockedMinutes() + " passed: " + TimeUnit.MINUTES.convert(timePassedLearningAid, TimeUnit.MILLISECONDS));

        if (timeLeft == TimeCredits.CREDIT_LEARNING.getBlockedMinutes() && mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            Log.d(getClass().toString(), "left: " + timeLeft + " blocked: " + TimeCredits.CREDIT_LEARNING.getBlockedMinutes() + " passed: " + TimeUnit.MINUTES.convert(timePassedLearningAid, TimeUnit.MILLISECONDS));

            commitStatus = false;
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
