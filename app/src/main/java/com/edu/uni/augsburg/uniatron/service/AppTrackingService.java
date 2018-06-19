package com.edu.uni.augsburg.uniatron.service;

import android.app.Notification;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.notification.builder.TimeUpNotificationBuilder;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.orhanobut.logger.Logger;
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
public class AppTrackingService extends Service {
    private static final int DELAY = 1000; //milliseconds
    private static final List<String> FILTERS = new ArrayList();
    private final AppChecker mAppChecker = new AppChecker();
    private SharedPreferencesHandler mSharedPreferencesHandler;
    private DataRepository mRepository;
    private final BroadcastReceiver mScreenEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Logger.d("ScreenOFF");
                stopAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Logger.d("ScreenON");
                startAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

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
        Logger.d("Service created");
        final MainApplication mainApplication = (MainApplication) getApplicationContext();
        mSharedPreferencesHandler = mainApplication.getSharedPreferencesHandler();
        mRepository = mainApplication.getRepository();

        startAppChecker();
    }

    private void startAppChecker() {
        mAppChecker.whenAny((String process) -> delegateAppUsage(process, DELAY))
                .timeout(DELAY)
                .start(getBaseContext());
    }

    private void stopAppChecker() {
        mAppChecker.stop();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
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
        //Log.d(getClass().toString(), "delegateAppUsage");
        commitAppUsageTime(appName, timeMillis);
        blockAppIfNecessary(appName);
        showTimesUpNotification();
    }

    private void showTimesUpNotification() {

        final Set<String> blackList = mSharedPreferencesHandler.getAppsBlacklist();
        mRepository.getRemainingAppUsageTimeToday(blackList).observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer remainingTime) {
                if (remainingTime == 5 || remainingTime == 1) {
                    final Context context = AppTrackingService.this.getApplicationContext();
                    final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(context, remainingTime);
                    final Notification notification = builder.build();
                    final int notificationId = builder.getId();
                    NotificationManagerCompat.from(context).notify(notificationId, notification);
                    mRepository.getRemainingAppUsageTimeToday(blackList).removeObserver(this);
                }

            }
        });
    }

    private void blockAppIfNecessary(final String appName) {
        final Set<String> blackList = mSharedPreferencesHandler.getAppsBlacklist();
        if (blackList.contains(appName)) {
            mRepository.getRemainingAppUsageTimeToday(blackList).observeForever(new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable final Integer integer) {
                    if (integer <= 0) {
                        // Log.d(getClass().toString(), "Integer: " +  integer);
                        // TODO Integer geht unter 0
                        final Intent intent = new Intent(AppTrackingService.this, MainActivity.class);
                        AppTrackingService.this.startActivity(intent);
                        mRepository.getRemainingAppUsageTimeToday(blackList).removeObserver(this);
                    }
                }
            });
        }
    }
}