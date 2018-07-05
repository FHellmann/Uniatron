package com.edu.uni.augsburg.uniatron.ui.day;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.model.Emotions;
import com.edu.uni.augsburg.uniatron.model.Summary;
import com.edu.uni.augsburg.uniatron.ui.day.card.SummaryCard;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayViewModel extends AndroidViewModel {
    private final MediatorLiveData<SummaryCard> mObservableDaySummary;
    private final DataRepository mRepository;

    public DayViewModel(@NonNull final Application application) {
        super(application);

        mRepository = MainApplication.getRepository(application);

        mObservableDaySummary = new MediatorLiveData<>();
    }

    public void setup(@NonNull final Date date, final int calendarType) {
        mObservableDaySummary.addSource(
                getSummarySourceBy(date, calendarType),
                value -> {
                    if (value != null && !value.isEmpty()) {
                        // There always be only one summary
                        final Summary summary = value.get(0);
                        mObservableDaySummary.setValue(new SummaryCard(summary));
                    }
                }
        );
    }

    private LiveData<List<Summary>> getSummarySourceBy(@NonNull final Date date,
                                                       final int calendarType) {
        switch (calendarType) {
            case Calendar.MONTH:
                return mRepository.getSummaryByMonth(
                        DateUtil.getMinDateOfMonth(date),
                        DateUtil.getMaxDateOfMonth(date)
                );
            case Calendar.YEAR:
                return mRepository.getSummaryByYear(
                        DateUtil.getMinMonthOfYear(date),
                        DateUtil.getMaxMonthOfYear(date)
                );
            case Calendar.DATE:
            default:
                return mRepository.getSummaryByDate(date, date);
        }
    }

    public LiveData<SummaryCard> getSummary() {
        return mObservableDaySummary;
    }
}
