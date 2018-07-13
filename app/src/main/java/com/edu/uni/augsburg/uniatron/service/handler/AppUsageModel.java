package com.edu.uni.augsburg.uniatron.service.handler;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.LearningAid;
import com.orhanobut.logger.Logger;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link AppUsageDetector}.
 *
 * @author Fabio Hellmann
 */
public class AppUsageModel {

    private final SharedPreferencesHandler mSharedPreferencesHandler;
    private final DataRepository mRepository;
    private final UsageTimeHelper mUsageTimeHelper = new UsageTimeHelper();
    private final LearningAidHelper mLearningAidHelper = new LearningAidHelper();
    private final SharedPreferences.OnSharedPreferenceChangeListener mPrefChangeListener;

    private final Map<Integer, Consumer<Integer>> mNotifyListeners = new LinkedHashMap<>();
    private Consumer<String> mBlockTimeOutListener;
    private Consumer<String> mBlockLearningAidListener;

    AppUsageModel(@NonNull final Context context) {
        mSharedPreferencesHandler = MainApplication.getSharedPreferencesHandler(context);
        mRepository = MainApplication.getRepository(context);

        mUsageTimeHelper.addLiveData(mRepository.getRemainingAppUsageTimeToday(getAppsBlacklist()));
        mLearningAidHelper.addLiveData(mRepository.getLatestLearningAid());

        mPrefChangeListener = (sharedPreferences, key) -> {
            final Set<String> blacklist = getAppsBlacklist();
            mUsageTimeHelper.addLiveData(mRepository.getRemainingAppUsageTimeToday(blacklist));
        };
        mSharedPreferencesHandler.registerOnPreferenceChangeListener(mPrefChangeListener);
    }

    /**
     * Destroys the model.
     */
    public void destroy() {
        mSharedPreferencesHandler.unregisterOnPreferenceChangeListener(mPrefChangeListener);
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
        mLearningAidHelper.addLiveData(mRepository.getLatestLearningAid());
        if (isAppNotInBlacklist(packageName)) {
            // Will catch all cases, when app name is not in the blacklist
            commitAppUsageTime(packageName, usageTime);
        } else if (mLearningAidHelper.isLearningAidActive()) {
            Logger.d("App '" + packageName + "' is blocked by learning aid -> BLOCKED");
            // 1. Priority: Check whether the learning aid is active or not
            if (mBlockLearningAidListener != null) {
                mBlockLearningAidListener.accept(packageName);
            }
        } else if (mUsageTimeHelper.getRemainingUsageTime() <= 0) {
            //Logger.d("App '" + appName + "' is blocked by time credit up -> BLOCKED");
            // 2. Priority: Check whether the time credit time is up or not
            if (mBlockTimeOutListener != null) {
                mBlockTimeOutListener.accept(packageName);
            }
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
    public void addNotifyOnTimeUpSoonListeners(@NonNull final Consumer<Integer> listener,
                                               @NonNull final Integer... timeToEnds) {
        Stream.ofNullable(timeToEnds)
                .filter(Objects::nonNull)
                .forEach(value -> mNotifyListeners.put(value, listener));
    }

    /**
     * Set listener for block by time out.
     *
     * @param listener The listener.
     */
    public void setBlockByTimeOutListener(@NonNull final Consumer<String> listener) {
        mBlockTimeOutListener = listener;
    }

    /**
     * Set listener for block by learning aid.
     *
     * @param listener The listener.
     */
    public void setBlockByLearningAidListener(@NonNull final Consumer<String> listener) {
        mBlockLearningAidListener = listener;
    }

    private void commitAppUsageTime(final String appName, final int usageTime) {
        if (!TextUtils.isEmpty(appName)) {
            mRepository.addAppUsage(appName, usageTime);
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
            if (learningAid == null) {
                mLearningAidTmp = new LearningAid(false, 0);
            } else {
                mLearningAidTmp = learningAid;
            }
        }

        private boolean isLearningAidActive() {
            return mLearningAidTmp.isActive();
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
