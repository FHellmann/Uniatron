package com.edu.uni.augsburg.uniatron.service;

import android.app.Notification;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.edu.uni.augsburg.uniatron.ui.home.shop.LearningAid;
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
    private final AppChecker mAppChecker = new AppChecker();
    private SharedPreferencesHandler mSharedPreferencesHandler;
    private DataRepository mRepository;
    private Boolean commitStatus = false;
    private int remainingUsageTime = 10;
    private Long timePassedLearningAid;

    private final BroadcastReceiver mScreenEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                //Logger.d("ScreenOFF");
                stopAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                //Logger.d("ScreenON");
                startAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            }
        }
    };
    private Observer<Integer> remainingUsageTimeObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable Integer integer) {
            //Log.d(getClass().toString(), "onchanged");
            remainingUsageTime = integer;
        }
    };

    private Observer<Long> learningAidObserver = new Observer<Long>() {
        @Override
        public void onChanged(@Nullable Long learningAidDiff) {
            timePassedLearningAid = learningAidDiff;
        }
    };



    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d(getClass().toString(), "AppTrackingService Created");
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");

        // get the default launcher
        final PackageManager localPackageManager = getPackageManager();
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        final String launcherPackageName = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;

        FILTERS.add(launcherPackageName);
        FILTERS.add("com.edu.uni.augsburg.uniatron");

        registerReceiver(mScreenEventReceiver, filter);
        //Logger.d("Service created");

        mSharedPreferencesHandler = MainApplication.getSharedPreferencesHandler(getBaseContext());
        mRepository = MainApplication.getRepository(getBaseContext());


        mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                .observe(this, remainingUsageTimeObserver);

        mRepository.getLatestLearningAidDiff().observe(this, learningAidObserver);

        startAppChecker();
    }

    private void startAppChecker() {
        mAppChecker.whenAny(process -> delegateAppUsage(process, DELAY_IN_MILLISECONDS))
                .timeout(DELAY_IN_MILLISECONDS)
                .start(getBaseContext());
    }

    private void stopAppChecker() {
        mAppChecker.stop();
    }

    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Log.d(getClass().toString(), "onDestroy");
        unregisterReceiver(mScreenEventReceiver);
        super.onDestroy();
    }

    private void commitAppUsageTime(final String appName, final int timeMillis) {
        if (!TextUtils.isEmpty(appName) && !FILTERS.contains(appName)) {
            final int time = (int) TimeUnit.SECONDS.convert(timeMillis, TimeUnit.MILLISECONDS);
            mRepository.addAppUsage(appName, time);
        }
    }

    private void delegateAppUsage(final String appName, final int timeMillis) {
        Log.d(getClass().toString(), "delegateAppUsage");
        blockAppIfNecessary(appName);
        blockLearningAid(appName);
        showNotificationIfNecessary();
        if (commitStatus || !mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            commitAppUsageTime(appName, timeMillis);
        }
    }

    private void showNotificationIfNecessary() {
        if (remainingUsageTime == 59 || remainingUsageTime == 299 || remainingUsageTime == 599) {
            final Context context = AppTrackingService.this.getApplicationContext();
            final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(context, remainingUsageTime + 1);
            final Notification notification = builder.build();
            final int notificationId = builder.getId();
            NotificationManagerCompat.from(context).notify(notificationId, notification);
        }
    }

    private void blockAppIfNecessary(final String appName) {
        Log.d(getClass().toString(), "Remaining Time: " + remainingUsageTime);
        if (remainingUsageTime == 0 && mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            commitStatus = false;

            final Intent blockIntent = new Intent(AppTrackingService.this, MainActivity.class);
            blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppTrackingService.this.startActivity(blockIntent);
        } else {
            commitStatus = true;
        }
    }

    private void blockLearningAid(final String appName) {
        final long timeLeft = TimeCredits.CREDIT_LEARNING.getBlockedMinutes()
                - TimeUnit.MINUTES.convert(timePassedLearningAid, TimeUnit.MILLISECONDS);
        if (timeLeft > 0
                && timeLeft <= TimeCredits.CREDIT_LEARNING.getBlockedMinutes()
                && mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            commitStatus = false;
            final Intent blockIntent = new Intent(AppTrackingService.this, MainActivity.class);
            blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppTrackingService.this.startActivity(blockIntent);
        }else{
            commitStatus = true;
        }
    }


}
