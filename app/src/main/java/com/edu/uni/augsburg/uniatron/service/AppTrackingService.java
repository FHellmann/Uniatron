package com.edu.uni.augsburg.uniatron.service;

import android.app.Notification;
import android.app.Service;
import android.arch.lifecycle.MediatorLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.Arrays;
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

    private static boolean time1min = false;
    private static boolean time5min = false;
    private static int lastTime;
    private static int currentTime;

    final MainApplication mainApplication = (MainApplication) getApplicationContext();
    final DataRepository repository = mainApplication.getRepository();

    private static final int DELAY = 1000; //milliseconds
    private static final List<String> FILTERS = Arrays.asList(
            "com.edu.uni.augsburg.uniatron"
    );

    private final AppChecker mAppChecker = new AppChecker();
    private final BroadcastReceiver mScreenEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Logger.d("ScreenOFF");
                stopAppChecker();
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Logger.d("ScreenON");
                startAppChecker();
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
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenEventReceiver, filter);
        Logger.d("Service created");

        startAppChecker();
    }

    private void startAppChecker() {
        mAppChecker.whenAny(process -> delegateAppUsage(process, DELAY))
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
            repository.addAppUsage(appName, time);
        }
    }

    private void delegateAppUsage(final String appName, final int timeMillis) {
        commitAppUsageTime(appName, timeMillis);
        blockAppIfNecessary(appName);
        showTimesUpNotification();
    }

    private void showTimesUpNotification() {

            //if (true) { //TODO add remaining app usage time

            //TODO create bool if time set and reset if time is increasing
            //TODO get LiveData from DataRepository
            final Context context = getApplicationContext();
            final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(context);
            final Notification notification = builder.build();
            final int notificationId = builder.getId();

            NotificationManagerCompat.from(context).notify(notificationId, notification);
       // }
    }

    private void blockAppIfNecessary(final String appName) {
        final MainApplication application = (MainApplication) getApplicationContext();
        final SharedPreferencesHandler sharedPreferencesHandler =
                application.getSharedPreferencesHandler();
        final Set<String> blackList = sharedPreferencesHandler.getAppsBlacklist();
        if (blackList.contains(appName)) {
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private int getRemainingTimeBlacklist(){
        final MediatorLiveData<Integer> mRemainingTime;
        mRemainingTime = new MediatorLiveData<>();
        mRemainingTime.addSource(
                repository.getRemainingAppUsageTimeToday(),
                mRemainingTime::setValue
        );
        return 1;
}

}
