package com.edu.uni.augsburg.uniatron.ui.card;

import android.app.Application;
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

public class AppStatisticsViewModel extends DateCacheViewModel implements CardViewModel {
    private final MediatorLiveData<Map<String, Integer>> mAppUsages;
    private final DataRepository mRepository;

    public AppStatisticsViewModel(@NonNull final Application application) {
        super(application);
        mRepository = MainApplication.getRepository(application);
        mAppUsages = new MediatorLiveData<>();
    }

    @Override
    public void setup(@NonNull final Date date, final int calendarType) {
        super.setup(date, calendarType);
        final LiveData<Map<String, Integer>> data = getAppUsageData(date, calendarType);
        register(mAppUsages, data);
        mAppUsages.addSource(
                data,
                mAppUsages::setValue
        );
    }

    @NonNull
    public LiveData<AppStatisticsCard> getAppStatisticsCard() {
        return Transformations.map(mAppUsages,
                data -> {
                    if (data != null && !data.isEmpty()) {
                        final AppStatisticsCard card = new AppStatisticsCard();
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
