package com.edu.uni.augsburg.uniatron.ui.card;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache to handle the date unregister.
 *
 * @param <T> The generic type of the source live data.
 * @author Fabio Hellmann
 */
class DateCache<T> {
    private final Map<MediatorLiveData<?>, LiveData<T>> mCache = new HashMap<>();

    /**
     * Register a new live data.
     *
     * @param liveData The live data to listen on.
     * @param source   The source live data.
     */
    void clearAndRegister(@NonNull final MediatorLiveData<?> liveData,
                          @NonNull final LiveData<T> source) {
        Stream.of(mCache.entrySet()).forEach(entry -> entry.getKey().removeSource(entry.getValue()));
        mCache.clear();
        mCache.put(liveData, source);
    }

    Date getDateFrom(@NonNull final Date date, final int calendarType) {
        switch (calendarType) {
            case Calendar.MONTH:
                return DateUtil.getMinDateOfMonth(date);
            case Calendar.YEAR:
                return DateUtil.getMinMonthOfYear(date);
            case Calendar.DATE:
            default:
                return DateUtil.getMinTimeOfDate(date);
        }
    }

    Date getDateTo(@NonNull final Date date, final int calendarType) {
        switch (calendarType) {
            case Calendar.MONTH:
                return DateUtil.getMaxDateOfMonth(date);
            case Calendar.YEAR:
                return DateUtil.getMaxMonthOfYear(date);
            case Calendar.DATE:
            default:
                return DateUtil.getMaxTimeOfDate(date);
        }
    }
}
