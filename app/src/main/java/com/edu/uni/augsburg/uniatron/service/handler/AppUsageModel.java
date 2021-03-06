package com.edu.uni.augsburg.uniatron.service.handler;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Objects;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.AppPreferences;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.TimeCreditDao;
import com.edu.uni.augsburg.uniatron.domain.dao.model.LearningAid;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The model is the connection between the data source
 * and the {@link AppUsageDetector}.
 *
 * @author Fabio Hellmann
 */
public class AppUsageModel {

    private final AppPreferences mSharedPreferencesHandler;
    private final UsageTimeHelper mUsageTimeHelper = new UsageTimeHelper();
    private final LearningAidHelper mLearningAidHelper = new LearningAidHelper();
    private final Map<Long, Consumer<Long>> mNotifyListeners = new LinkedHashMap<>();
    private final Consumer<String> mBlockTimeOutListener;
    private final Consumer<String> mBlockLearningAidListener;
    private final AppUsageDao mAppUsageDao;
    private final TimeCreditDao mTimeCreditDao;

    AppUsageModel(@NonNull final Context context,
                  @NonNull final Consumer<String> blockTimeOutListener,
                  @NonNull final Consumer<String> blockLearningAidListener) {
        final AppContext application = AppContext.getInstance(context);
        mSharedPreferencesHandler = application.getPreferences();
        mAppUsageDao = application.getAppUsageDao();
        mTimeCreditDao = application.getTimeCreditDao();
        mBlockTimeOutListener = blockTimeOutListener;
        mBlockLearningAidListener = blockLearningAidListener;

        mUsageTimeHelper.addLiveData(mAppUsageDao.getRemainingAppUsageTimeToday(getAppsBlacklist()));
        mLearningAidHelper.addLiveData(mTimeCreditDao.getLatestLearningAid());

        registerPreferenceListener();
    }

    private void registerPreferenceListener() {
        mSharedPreferencesHandler.registerListener(
                SharedPreferencesHandler.PREF_APP_BLACKLIST,
                pref -> mUsageTimeHelper.addLiveData(mAppUsageDao.getRemainingAppUsageTimeToday(getAppsBlacklist()))
        );
    }

    /**
     * Destroys the model.
     */
    public void destroy() {
        mSharedPreferencesHandler.removeListener(SharedPreferencesHandler.PREF_APP_BLACKLIST);
        mUsageTimeHelper.removeLiveData();
        mLearningAidHelper.removeLiveData();
    }

    /**
     * Called when an app is used.
     *
     * @param packageName The package name of the used app.
     * @param usageTime   The usage time.
     */
    public void onAppUsed(@NonNull final String packageName, final int usageTime) {
        mLearningAidHelper.addLiveData(mTimeCreditDao.getLatestLearningAid());
        if (isAppNotInBlacklist(packageName)) {
            // Will catch all cases, when app name is not in the blacklist
            commitAppUsageTime(packageName, usageTime);
        } else if (mLearningAidHelper.isLearningAidActive()) {
            Logger.d("App '" + packageName + "' is blocked by learning aid -> BLOCKED");
            // 1. Priority: Check whether the learning aid is active or not
            mBlockLearningAidListener.accept(packageName);
        } else if (mUsageTimeHelper.getRemainingUsageTime() <= 0) {
            //Logger.d("App '" + appName + "' is blocked by time credit up -> BLOCKED");
            // 2. Priority: Check whether the time credit time is up or not
            mBlockTimeOutListener.accept(packageName);
        } else {
            Stream.of(mNotifyListeners.entrySet())
                    .filter(entry -> entry.getKey() == mUsageTimeHelper.getRemainingUsageTime())
                    .forEach(entry -> entry.getValue().accept(entry.getKey()));
            // x. Priority: Every other case...
            commitAppUsageTime(packageName, usageTime);
        }
    }

    /**
     * Add listeners for notification on time up soon.
     *
     * @param listener   The listener.
     * @param timeToEnds The times.
     */
    public void addNotifyOnTimeUpSoonListeners(@NonNull final Consumer<Long> listener,
                                               @NonNull final Long... timeToEnds) {
        Stream.ofNullable(timeToEnds)
                .filter(Objects::nonNull)
                .forEach(value -> mNotifyListeners.put(value, listener));
    }

    private void commitAppUsageTime(final String appName, final int usageTime) {
        if (!TextUtils.isEmpty(appName)) {
            mAppUsageDao.addAppUsage(appName, usageTime);
        }
    }

    private boolean isAppNotInBlacklist(@NonNull final String packageName) {
        return !getAppsBlacklist().contains(packageName);
    }

    @NonNull
    private Set<String> getAppsBlacklist() {
        return mSharedPreferencesHandler.getAppsBlacklist();
    }

    /**
     * Get the package names which has to be filtered out.
     *
     * @param context The context.
     * @return The filtered package names.
     */
    public List<String> getPackageFilters(@NonNull final Context context) {
        return Arrays.asList(context.getPackageName(), getDefaultLauncherPackageName(context));
    }

    private String getDefaultLauncherPackageName(@NonNull final Context context) {
        final PackageManager localPackageManager = context.getPackageManager();
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .activityInfo.packageName;
    }

    private static final class UsageTimeHelper implements Observer<Long> {
        private final MediatorLiveData<Long> mUsageTimeMediator = new MediatorLiveData<>();
        // initialize with a value > 0
        // in case we use the value before the observer reports it
        private long mRemainingUsageTime = 10;
        private LiveData<Long> mLiveData;
        private boolean mReset;

        @Override
        public void onChanged(@Nullable final Long remainingUsageTimeDB) {
            if (remainingUsageTimeDB == null) {
                mRemainingUsageTime = 10;
            } else {
                mRemainingUsageTime = remainingUsageTimeDB;
            }
        }

        private long getRemainingUsageTime() {
            return mRemainingUsageTime;
        }

        private void addLiveData(@NonNull final LiveData<Long> liveData) {
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

    private static final class LearningAidHelper implements Observer<LearningAid> {
        private final MediatorLiveData<LearningAid> mLearningAidMediator = new MediatorLiveData<>();
        private LearningAid mLearningAidTmp;
        private LiveData<LearningAid> mLiveData;
        private boolean mReset;

        @Override
        public void onChanged(@Nullable final LearningAid learningAid) {
            mLearningAidTmp = learningAid;
        }

        private boolean isLearningAidActive() {
            return Optional.ofNullable(mLearningAidTmp).map(tmp -> tmp.getLeftTime().isPresent()).orElse(false);
        }

        private void addLiveData(@NonNull final LiveData<LearningAid> liveData) {
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
