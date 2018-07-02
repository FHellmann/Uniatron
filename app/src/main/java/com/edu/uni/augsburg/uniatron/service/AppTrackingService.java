package com.edu.uni.augsburg.uniatron.service;

import android.app.Notification;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.TimeCredits;
import com.edu.uni.augsburg.uniatron.notification.builder.AidFinishNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.builder.TimeUpNotificationBuilder;
import com.edu.uni.augsburg.uniatron.ui.MainActivity;
import com.edu.uni.augsburg.uniatron.ui.home.shop.TimeCreditShopActivity;
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
public class AppTrackingService extends LifecycleService {

    private static final int DELAY_IN_MILLISECONDS = 1000;
    private static final List<String> FILTERS = new ArrayList<>();
    private static final int NOTIFICATION_ONE_MINUTE = 59;
    private static final int NOTIFICATION_FIVE_MINUTES = 299;
    private static final int NOTIFICATION_TEN_MINUTES = 599;

    private final AppChecker mAppChecker = new AppChecker();
    private final UsageTimeHelper mUsageTimeHelper = new UsageTimeHelper();
    private final LearningAidHelper mLearningAidHelper = new LearningAidHelper();
    private SharedPreferencesHandler mSharedPreferencesHandler;
    private DataRepository mRepository;

    private final BroadcastReceiver mScreenEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                stopAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                startAppChecker();
                startService(new Intent(getBaseContext(), StepCountService.class));
            }
        }
    };
    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefChangeListener =
            (sharedPreferences, key) -> {
                final Set<String> blacklist = getAppsBlacklist();
                mUsageTimeHelper.addLiveData(mRepository.getRemainingAppUsageTimeToday(blacklist));
            };

    @Override
    public void onCreate() {
        super.onCreate();

        FILTERS.add(getDefaultLauncherPackageName());
        FILTERS.add(getApplicationContext().getPackageName());

        //register Events ScreenOn and ScreenOff
        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenEventReceiver, filter);

        mSharedPreferencesHandler = MainApplication.getSharedPreferencesHandler(getBaseContext());
        mRepository = MainApplication.getRepository(getBaseContext());

        mUsageTimeHelper.addLiveData(mRepository.getRemainingAppUsageTimeToday(getAppsBlacklist()));
        mLearningAidHelper.addLiveData(mRepository.getLatestLearningAidDiff());

        mSharedPreferencesHandler.registerOnPreferenceChangeListener(mPrefChangeListener);

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
        mUsageTimeHelper.removeLiveData();
        mLearningAidHelper.removeLiveData();
        mSharedPreferencesHandler.unregisterOnPreferenceChangeListener(mPrefChangeListener);
        super.onDestroy();
    }

    @NonNull
    private Set<String> getAppsBlacklist() {
        return mSharedPreferencesHandler.getAppsBlacklist();
    }

    // called periodically and handles all app blocking logic
    private void delegateAppUsage(final String appName, final int timeMillis) {
        mLearningAidHelper.addLiveData(mRepository.getLatestLearningAidDiff());

        //Logger.d("Remaining Time Usage=" + mUsageTimeHelper.getRemainingUsageTime()
        // + ", Learning aid active=" + mLearningAidHelper.isLearningAidActive());

        if (!getAppsBlacklist().contains(appName)) {
            //Logger.d("App '" + appName + "' is not on the blacklist -> SAVED");
            // Will catch all cases, when app name is not in the blacklist otherwise else...
            commitAppUsageTime(appName, timeMillis);
            return;
        }
        if (mLearningAidHelper.isLearningAidActive()) {
            Logger.d("App '" + appName + "' is blocked by learning aid -> BLOCKED");
            // 1. Priority: Check whether the learning aid is active or not
            blockByLearningAid();
        } else if (mUsageTimeHelper.getRemainingUsageTime() <= 0) {
            Logger.d("App '" + appName + "' is blocked by time credit up -> BLOCKED");
            // 2. Priority: Check whether the time credit time is up or not
            blockByTimeCreditIfTimeUp();
        } else if (mUsageTimeHelper.getRemainingUsageTime() == NOTIFICATION_ONE_MINUTE
                || mUsageTimeHelper.getRemainingUsageTime() == NOTIFICATION_FIVE_MINUTES
                || mUsageTimeHelper.getRemainingUsageTime() == NOTIFICATION_TEN_MINUTES) {
            //Logger.d("Time is running out -> NOTIFICATION");
            // 3. Priority: Show a notification, when the time is running out
            showNotificationIfTimeAlmostUp();
            commitAppUsageTime(appName, timeMillis);
        } else {
            //Logger.d("App '" + appName + "' usage -> SAVED");
            // x. Priority: Every other case...
            commitAppUsageTime(appName, timeMillis);
        }
    }

    private void showNotificationIfTimeAlmostUp() {
        final Context context = getApplicationContext();
        final TimeUpNotificationBuilder builder = new TimeUpNotificationBuilder(context, mUsageTimeHelper.getRemainingUsageTime() + 1);
        final Notification notification = builder.build();
        final int notificationId = builder.getId();
        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    private void blockByTimeCreditIfTimeUp() {
        final Intent blockIntent = new Intent(getApplicationContext(), MainActivity.class);
        blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(blockIntent);
    }

    private void blockByLearningAid() {
        if (mLearningAidHelper.getLearningAidDiffMillis()
                == TimeCredits.CREDIT_LEARNING.getBlockedMinutes() * 60 * 1000 - 1) {
            //Logger.d("Learning aid time is up -> NOTIFICATION!");
            final Context context = getApplicationContext();
            final AidFinishNotificationBuilder builder = new AidFinishNotificationBuilder(context);
            final Notification notification = builder.build();
            final int notificationId = builder.getId();
            NotificationManagerCompat.from(context).notify(notificationId, notification);
        }

        final Intent blockIntent = new Intent(getApplicationContext(), TimeCreditShopActivity.class);
        blockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(blockIntent);
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

    private static final class UsageTimeHelper implements Observer<Integer> {
        private final MediatorLiveData<Integer> mUsageTimeMediator = new MediatorLiveData<>();
        // initialize with a value > 0
        // in case we use the value before the observer reports it
        private int mRemainingUsageTime = 10;
        private LiveData<Integer> mLiveData;
        private boolean mReset;

        @Override
        public void onChanged(@Nullable final Integer remainingUsageTimeDB) {
            if (remainingUsageTimeDB == null) {
                mRemainingUsageTime = 10;
            } else {
                mRemainingUsageTime = remainingUsageTimeDB;
            }
        }

        private int getRemainingUsageTime() {
            return mRemainingUsageTime;
        }

        private void addLiveData(@NonNull final LiveData<Integer> liveData) {
            synchronized (this) {
                if (mReset) {
                    removeLiveData();
                }
                mLiveData = liveData;
                mUsageTimeMediator.addSource(mLiveData, mUsageTimeMediator::setValue);
                mUsageTimeMediator.observeForever(this);
                mReset = true;
            }
        }

        private void removeLiveData() {
            mUsageTimeMediator.removeObserver(this);
            mUsageTimeMediator.removeSource(mLiveData);
            mReset = false;
        }
    }

    private static final class LearningAidHelper implements Observer<Long> {
        private final MediatorLiveData<Long> mLearningAidMediator = new MediatorLiveData<>();
        private Long mLearningAidDiffMillis = 0L;
        private LiveData<Long> mLiveData;
        private boolean mReset;

        @Override
        public void onChanged(@Nullable final Long learningAidDiff) {
            if (learningAidDiff == null) {
                mLearningAidDiffMillis = 0L;
            } else {
                mLearningAidDiffMillis = learningAidDiff;
            }
        }

        private boolean isLearningAidActive() {
            final long timeBlockedMinutes = TimeCredits.CREDIT_LEARNING.getBlockedMinutes();
            final long timePassedMinutes = TimeUnit.MINUTES
                    .convert(mLearningAidDiffMillis, TimeUnit.MILLISECONDS);

            return mLearningAidDiffMillis > 0 && timePassedMinutes < timeBlockedMinutes;
        }

        private long getLearningAidDiffMillis() {
            return mLearningAidDiffMillis;
        }

        private void addLiveData(@NonNull final LiveData<Long> liveData) {
            synchronized (this) {
                if (mReset) {
                    removeLiveData();
                }
                mLiveData = liveData;
                mLearningAidMediator.addSource(mLiveData, mLearningAidMediator::setValue);
                mLearningAidMediator.observeForever(this);
                mReset = true;
            }
        }

        private void removeLiveData() {
            mLearningAidMediator.removeObserver(this);
            mLearningAidMediator.removeSource(mLiveData);
            mReset = false;
        }
    }
}
