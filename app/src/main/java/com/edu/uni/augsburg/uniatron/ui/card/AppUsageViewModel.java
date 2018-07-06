package com.edu.uni.augsburg.uniatron.ui.card;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;

import java.util.Calendar;
import java.util.Date;
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
        mDateCache.unregister();

        final LiveData<Map<String, Integer>> data = getAppUsageData(date, calendarType);
        mDateCache.register(mAppUsages, data);
        mAppUsages.addSource(
                data,
                mAppUsages::setValue
        );
    }

    @NonNull
    public LiveData<AppUsageCard> getAppStatisticsCard() {
        return Transformations.map(mAppUsages,
                data -> {
                    if (data != null && !data.isEmpty()) {
                        final AppUsageCard card = new AppUsageCard();
                        card.addAll(data);
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
