package com.edu.uni.augsburg.uniatron.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

import java.util.Set;

/**
 * The {@link MainActivityViewModel} provides the data for the {@link MainActivity}.
 *
 * @author Fabio Hellmann
 */
public class MainActivityViewModel extends AndroidViewModel {
    private final MediatorLiveData<Integer> mRemainingStepCount;
    private final MediatorLiveData<Integer> mRemainingAppUsageTime;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public MainActivityViewModel(@NonNull final Application application) {
        super(application);

        final DataRepository repository = MainApplication.getRepository(application);


        mRemainingStepCount = new MediatorLiveData<>();
        mRemainingStepCount.addSource(
                repository.getRemainingStepCountsToday(),
                mRemainingStepCount::setValue
        );

        final SharedPreferencesHandler handler = MainApplication.
                getSharedPreferencesHandler(application);
        final Set<String> blacklist = handler.getAppsBlacklist();

        mRemainingAppUsageTime = new MediatorLiveData<>();
        mRemainingAppUsageTime.addSource(
                repository.getRemainingAppUsageTimeToday(blacklist),
                mRemainingAppUsageTime::setValue
        );
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The remaining step count.
     */
    @NonNull
    public LiveData<Integer> getRemainingStepCountToday() {
        return Transformations.map(mRemainingStepCount,
                data -> data != null && data > 0 ? data : 0);
    }

    /**
     * Get the remaining app usage time for today.
     *
     * @return The remaining app usage time.
     */
    @NonNull
    public LiveData<Integer> getRemainingAppUsageTime() {
        return Transformations.map(mRemainingAppUsageTime,
                data -> data != null && data > 0 ? data : 0);
    }
}
