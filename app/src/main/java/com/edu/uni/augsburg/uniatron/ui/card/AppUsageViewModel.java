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

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link AppUsageCard}.
 *
 * @author Fabio Hellmann
 */
public class AppUsageViewModel extends AndroidViewModel implements CardViewModel {
    private final DateCache<Map<String, Integer>> mDateCache;
    private final MediatorLiveData<Map<String, Integer>> mAppUsages;
    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public AppUsageViewModel(@NonNull final Application application) {
        super(application);
        mDateCache = new DateCache<>();
        mRepository = MainApplication.getRepository(application);
        mAppUsages = new MediatorLiveData<>();
    }

    @Override
    public void setup(@NonNull final Date date, final int calendarType) {
        final LiveData<Map<String, Integer>> data = getAppUsageData(date, calendarType);
        mDateCache.clearAndRegister(mAppUsages, data);
        mAppUsages.addSource(
                data,
                mAppUsages::setValue
        );
    }

    @NonNull
    public LiveData<AppUsageCard> getAppStatisticsCard(@NonNull final Context context) {
        return Transformations.map(mAppUsages,
                data -> {
                    if (data != null && !data.isEmpty()) {
                        final AppUsageCard card = new AppUsageCard();

                        final int usageTimeSum = Stream.of(data).mapToInt(Map.Entry::getValue).sum();

                        final List<AppUsageCard.AppUsageItem> appUsageItems = Stream.of(data)
                                .map(entry -> {
                                    final String applicationPackage = entry.getKey();
                                    final int usageTime = entry.getValue();
                                    final double usageTimePercent = usageTime * 100.0 / usageTimeSum;

                                    return new AppUsageCard.AppUsageItem(
                                            applicationPackage,
                                            usageTime,
                                            usageTimePercent
                                    );
                                })
                                .collect(Collectors.toList());

                        card.addAll(appUsageItems);
                        return card;
                    }
                    return null;
                });
    }

    @NonNull
    private LiveData<Map<String, Integer>> getAppUsageData(@NonNull final Date date,
                                                           final int calendarType) {
        switch (calendarType) {
            case Calendar.MONTH:
                return mRepository.getAppUsageTimeByDate(
                        DateUtil.getMinDateOfMonth(date),
                        DateUtil.getMaxDateOfMonth(date)
                );
            case Calendar.YEAR:
                return mRepository.getAppUsageTimeByDate(
                        DateUtil.getMinMonthOfYear(date),
                        DateUtil.getMaxMonthOfYear(date)
                );
            case Calendar.DATE:
            default:
                return mRepository.getAppUsageTimeByDate(
                        DateUtil.getMinTimeOfDate(date),
                        DateUtil.getMaxTimeOfDate(date)
                );
        }
    }
}
