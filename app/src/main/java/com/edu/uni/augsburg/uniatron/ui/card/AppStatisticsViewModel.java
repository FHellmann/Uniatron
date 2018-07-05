package com.edu.uni.augsburg.uniatron.ui.card;

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
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppStatisticsViewModel extends DateCacheViewModel implements CardViewModel {
    /**
     * The maximum count of apps shown as top n apps.
     */
    private static final int MAX_COUNT = 4;
    private final MediatorLiveData<Map<String, Double>> mAppUsages;

    public AppStatisticsViewModel(@NonNull Application application) {
        super(application);

        final DataRepository repository = MainApplication.getRepository(application);

        mAppUsages = new MediatorLiveData<>();
        final LiveData<Map<String, Double>> data = repository.getAppUsagePercentToday();
        register(mAppUsages, data);
        mAppUsages.addSource(
                data,
                mAppUsages::setValue
        );
    }

    @Override
    public void setup(Date date, int calendarType) {
        super.setup(date, calendarType);
    }

    @NonNull
    public LiveData<AppStatisticsCard> getAppStatisticsCard() {
        return Transformations.map(mAppUsages, data -> {
            if (data != null && !data.isEmpty()) {
                final AppStatisticsCard card = new AppStatisticsCard();
                Stream.of(data.entrySet())
                        .map(entry -> new PieEntry(entry.getValue().floatValue(), entry.getKey()))
                        .forEach(card::addEntry);

                if (data.size() == MAX_COUNT) {
                    final float value = (float) Stream.of(data.values())
                            .mapToDouble(dValue -> dValue)
                            .sum();
                    card.addEntry(new PieEntry(1 - value, getApplication().getString(R.string.app_others)));
                }
                return card;
            }
            return null;
        });
    }

    /**
     * Get the app usage of the top 5 apps.
     *
     * @param context The context.
     * @return The app usage.
     */
    @NonNull
    private LiveData<Map<String, Double>> getAppUsageOfTop5Apps(@NonNull final Context context) {
        return Transformations.map(mAppUsages, data -> extractValues(context, data, MAX_COUNT));
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
