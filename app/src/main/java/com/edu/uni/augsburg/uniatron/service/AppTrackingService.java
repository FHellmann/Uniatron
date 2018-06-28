package com.edu.uni.augsburg.uniatron.service;

import android.app.Notification;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.notification.builder.TimeUpNotificationBuilder;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.orhanobut.logger.Logger;
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
public class AppTrackingService extends Service {

    private static final int DELAY_IN_MILLISECONDS = 1000;
    private static final List<String> FILTERS = new ArrayList<>();
    private final AppChecker mAppChecker = new AppChecker();
    private SharedPreferencesHandler mSharedPreferencesHandler;
    private DataRepository mRepository;
    private Boolean commitStatus = false;
    private int lastRecordedAppUsageTime;
    private final Observer<Integer> appUsageTimeObserver = new Observer<Integer>() {
        @Override
        public void onChanged(@Nullable final Integer remainingTime) {
            Logger.d("in onChanged");
            if (remainingTime == null) {
                Logger.d("nullpointerexc");
            }
            else {
                lastRecordedAppUsageTime = remainingTime;
            }
        }
    };


    //TODO hier wird die Blacklist noch nicht
    // aktualisiert außer man startet die App neu
    private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener
            = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                    .removeObserver(appUsageTimeObserver);
            mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                    .observeForever(appUsageTimeObserver);
        }
    };
    /*
        private final Observer<Integer> observerAppUsage = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer remainingTime) {
                final Set<String> blackList = mSharedPreferencesHandler.getAppsBlacklist();
                if (remainingTime == 0) {
                    commitStatus = false;
                    final Intent intent = new Intent(AppTrackingService.this, MainActivity.class);
                    AppTrackingService.this.startActivity(intent);
                } else {
                    commitStatus = true;
                }
                Log.d(getClass().toString(), "Integer: " + remainingTime); //TODO Integer geht unter 0
                Log.d(getClass().toString(), "CommitSatus: " + commitStatus);
            }
        };
    */
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

        mSharedPreferencesHandler = MainApplication.getSharedPreferencesHandler(getBaseContext());
        mRepository = MainApplication.getRepository(getBaseContext());

        mSharedPreferencesHandler.registerOnPreferenceChangeListener(onSharedPreferenceChangeListener);

        mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                .observeForever(appUsageTimeObserver);

        startAppChecker();
    }

    private void startAppChecker() {
        mAppChecker.whenAny(process -> commitAppUsageTime(process, DELAY_IN_MILLISECONDS))
                .timeout(DELAY_IN_MILLISECONDS)
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
        Logger.d("onDestroy");
        unregisterReceiver(mScreenEventReceiver);

        mRepository.getRemainingAppUsageTimeToday(mSharedPreferencesHandler.getAppsBlacklist())
                .removeObserver(appUsageTimeObserver);

        mSharedPreferencesHandler.
                unRegisterOnPreferenceChangeListener(onSharedPreferenceChangeListener);

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
        blockAppIfNecessary(appName);
        showTimesUpNotificationIfNecessary();

        if (commitStatus || !mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            commitAppUsageTime(appName, timeMillis);
        }
    }

    private void showTimesUpNotificationIfNecessary() {
        if (lastRecordedAppUsageTime == 59 || lastRecordedAppUsageTime == 299 || lastRecordedAppUsageTime == 599) {
            final Context context = AppTrackingService.this.getApplicationContext();
            final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(context, lastRecordedAppUsageTime + 1);
            final Notification notification = builder.build();
            final int notificationId = builder.getId();
            NotificationManagerCompat.from(context).notify(notificationId, notification);
        }
    }


    private void blockAppIfNecessary(final String appName) {
        Log.d(getClass().toString(), "Remaining Time: " + lastRecordedAppUsageTime);
        if (lastRecordedAppUsageTime == 0 && mSharedPreferencesHandler.getAppsBlacklist().contains(appName)) {
            commitStatus = false;

            final Intent blockIntent = new Intent(AppTrackingService.this, MainActivity.class);
            blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppTrackingService.this.startActivity(blockIntent);
        } else {
            commitStatus = true;
        }
    }
}
