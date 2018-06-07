package com.edu.uni.augsburg.uniatron.ui.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link HomeViewModel} provides the data for the {@link HomeFragment}.
 *
 * @author Fabio Hellmann
 */
public class HomeViewModel extends AndroidViewModel {
    /** The maximum count of apps shown as top n apps. */
    public static final int MAX_COUNT = 5;
    private final MediatorLiveData<Map<String, Double>> mAppUsages;
    private final MediatorLiveData<Integer> mRemainingStepCount;
    private final MediatorLiveData<Integer> mRemainingAppUsageTime;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public HomeViewModel(@NonNull final Application application) {
        super(application);

        final DataRepository repository = ((MainApplication) application).getRepository();

        mAppUsages = new MediatorLiveData<>();
        mAppUsages.addSource(
                repository.getAppUsagePercentToday(),
                mAppUsages::setValue
        );

        mRemainingStepCount = new MediatorLiveData<>();
        mRemainingStepCount.addSource(
                repository.getRemainingStepCountsToday(),
                mRemainingStepCount::setValue
        );

        mRemainingAppUsageTime = new MediatorLiveData<>();
        mRemainingAppUsageTime.addSource(
                repository.getRemainingAppUsageTimeToday(),
                mRemainingAppUsageTime::setValue
        );
    }

    /**
     * Get the app usage of the top 3 apps.
     *
     * @param context The context.
     * @return The app usage.
     */
    @NonNull
    public LiveData<Map<String, Double>> getAppUsageOfTop5Apps(@NonNull final Context context) {
        return Transformations.map(mAppUsages, data -> extractValues(context, data, MAX_COUNT));
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

    @NonNull
    private Map<String, Double> extractValues(final Context context,
                                              final Map<String, Double> data,
                                              final int maxCount) {
        if (data == null) {
            return Collections.emptyMap();
        }
        // 1. Convert Map to List of Map
        final List<Map.Entry<String, Double>> list = new LinkedList<>(data.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, (value1, value2) -> value2.getValue().compareTo(value1.getValue()));

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        final Map<String, Double> sortedMap = new ConcurrentHashMap<>();
        for (int index = 0; index < list.size() && sortedMap.size() < maxCount; index++) {
            final Map.Entry<String, Double> entry = list.get(index);
            getApplicationLabel(context, entry.getKey())
                    .ifPresent(appLabel -> sortedMap.put(appLabel, entry.getValue()));
        }
        return sortedMap;
    }

    @NonNull
    private Optional<String> getApplicationLabel(@NonNull final Context context,
                                                 @NonNull final String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);
        return Stream.of(installedApplications)
                .filter(app -> app.packageName.equals(packageName))
                .findFirst()
                .map(appInfo -> packageManager.getApplicationLabel(appInfo).toString());
    }
}
