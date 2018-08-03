package com.edu.uni.augsburg.uniatron.ui.card.summary;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.dao.SummaryDao;
import com.edu.uni.augsburg.uniatron.domain.dao.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Summary;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.DateCache;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The model is the connection between the data source
 * and the {@link SummaryCard}.
 *
 * @author Fabio Hellmann
 */
public class SummaryViewModel extends AndroidViewModel implements CardViewModel {
    private final DateCache<List<Summary>> mDateCache;
    private final MediatorLiveData<SummaryCard> mObservableDaySummary;
    private final SummaryDao mSummaryDao;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public SummaryViewModel(@NonNull final Application application) {
        super(application);
        mSummaryDao = MainApplication.getInstance(application).getSummaryDao();
        mDateCache = new DateCache<>();
        mObservableDaySummary = new MediatorLiveData<>();
    }

    @Override
    public void setup(@NonNull final Date date, final int calendarType) {
        final LiveData<List<Summary>> source = getSummarySourceBy(date, calendarType);
        mDateCache.clearAndRegister(mObservableDaySummary, source);
        mObservableDaySummary.addSource(
                source,
                value -> {
                    if (value != null && !value.isEmpty()) {
                        // There always be only one summary
                        final Summary summary = value.get(0);
                        mObservableDaySummary.setValue(new SummaryCard(summary));
                    }
                }
        );
    }

    private LiveData<List<Summary>> getSummarySourceBy(@NonNull final Date date, final int calendarType) {
        final Date dateFrom = DateConverter.getMin(calendarType).convert(date);
        final Date dateTo = DateConverter.getMax(calendarType).convert(date);
        switch (calendarType) {
            case Calendar.MONTH:
                return mSummaryDao.getSummaryByMonth(dateFrom, dateTo);
            case Calendar.YEAR:
                return mSummaryDao.getSummaryByYear(dateFrom, dateTo);
            case Calendar.DATE:
            default:
                return mSummaryDao.getSummaryByDate(dateFrom, dateTo);
        }
    }

    /**
     * Get the summary card.
     *
     * @return The summary card.
     */
    public LiveData<SummaryCard> getSummaryCard() {
        return mObservableDaySummary;
    }
}
