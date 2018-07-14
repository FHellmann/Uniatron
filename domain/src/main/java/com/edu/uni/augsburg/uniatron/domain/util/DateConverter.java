package com.edu.uni.augsburg.uniatron.domain.util;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Function;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This is a util class to convert dates.
 *
 * @author Fabio Hellmann
 */
public enum DateConverter {
    /**
     * Converts the given date to the minimum time of the day.
     */
    DATE_MIN_TIME(date -> {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }),
    /**
     * Converts the given date to the maximum time of the day.
     */
    DATE_MAX_TIME(date -> {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, Constants.MAX_HOUR_OF_DAY);
        calendar.set(Calendar.MINUTE, Constants.MAX_MINUTE);
        calendar.set(Calendar.SECOND, Constants.MAX_SECOND);
        calendar.set(Calendar.MILLISECOND, Constants.MAX_MILLISECOND);
        return calendar.getTime();
    }),
    /**
     * Converts the given date to the minimum day with minimum time.
     */
    MONTH_MIN_DATE(date -> {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.setTime(DATE_MIN_TIME.convert(calendar.getTime()));
        return calendar.getTime();
    }),
    /**
     * Converts the given date to the minimum day with minimum time.
     */
    MONTH_MAX_DATE(date -> {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.setTime(DATE_MAX_TIME.convert(calendar.getTime()));
        return calendar.getTime();
    }),
    /**
     * Converts the given date to the minimum month and day with minimum time.
     */
    YEAR_MIN_MONTH(date -> {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 0);
        calendar.setTime(MONTH_MIN_DATE.convert(calendar.getTime()));
        return calendar.getTime();
    }),
    /**
     * Converts the given date to the minimum month and day with minimum time.
     */
    YEAR_MAX_MONTH(date -> {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.setTime(MONTH_MAX_DATE.convert(calendar.getTime()));
        return calendar.getTime();
    });

    @NonNull
    private final Function<Date, Date> mFunction;

    DateConverter(@NonNull final Function<Date, Date> function) {
        mFunction = function;
    }

    /**
     * Converts the date.
     *
     * @param date The date to convert.
     * @return The converted date.
     */
    public Date convert(@NonNull final Date date) {
        return mFunction.apply(date);
    }

    /**
     * Get the date converter for the calendar grouping.
     *
     * @param calendarGrouping The calendar grouping by date, month or year.
     * @return The date converter.
     */
    public static DateConverter getDateConverterMin(final int calendarGrouping) {
        switch (calendarGrouping) {
            case Calendar.MONTH:
                return DateConverter.MONTH_MIN_DATE;
            case Calendar.YEAR:
                return DateConverter.YEAR_MIN_MONTH;
            case Calendar.DATE:
            default:
                return DateConverter.DATE_MIN_TIME;
        }
    }

    /**
     * Get the date converter for the calendar grouping.
     *
     * @param calendarGrouping The calendar grouping by date, month or year.
     * @return The date converter.
     */
    public static DateConverter getDateConverterMax(final int calendarGrouping) {
        switch (calendarGrouping) {
            case Calendar.MONTH:
                return DateConverter.MONTH_MAX_DATE;
            case Calendar.YEAR:
                return DateConverter.YEAR_MAX_MONTH;
            case Calendar.DATE:
            default:
                return DateConverter.DATE_MAX_TIME;
        }
    }

    private static final class Constants {
        private static final int MAX_HOUR_OF_DAY = 23;
        private static final int MAX_MINUTE = 59;
        private static final int MAX_SECOND = 59;
        private static final int MAX_MILLISECOND = 999;
    }
}
