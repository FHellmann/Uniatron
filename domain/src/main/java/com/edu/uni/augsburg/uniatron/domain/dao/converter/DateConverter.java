package com.edu.uni.augsburg.uniatron.domain.dao.converter;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

/**
 * A class to convert dates.
 *
 * @author Fabio Hellmann
 */
public interface DateConverter {
    /**
     * Converts the date.
     *
     * @param date The date to convert.
     * @return The converted date.
     */
    Date convert(@NonNull Date date);

    /**
     * Converts the current date.
     *
     * @return The converted date.
     */
    Date now();

    /**
     * Get the date converter for the calendar grouping.
     *
     * @param calendarGrouping The calendar grouping by date, month or year.
     * @return The date converter.
     */
    static DateConverter getMin(final int calendarGrouping) {
        switch (calendarGrouping) {
            case Calendar.MONTH:
                return DateConverterImpl.MONTH_MIN_DATE;
            case Calendar.YEAR:
                return DateConverterImpl.YEAR_MIN_MONTH;
            case Calendar.DATE:
            default:
                return DateConverterImpl.DATE_MIN_TIME;
        }
    }

    /**
     * Get the date converter for the calendar grouping.
     *
     * @param calendarGrouping The calendar grouping by date, month or year.
     * @return The date converter.
     */
    static DateConverter getMax(final int calendarGrouping) {
        switch (calendarGrouping) {
            case Calendar.MONTH:
                return DateConverterImpl.MONTH_MAX_DATE;
            case Calendar.YEAR:
                return DateConverterImpl.YEAR_MAX_MONTH;
            case Calendar.DATE:
            default:
                return DateConverterImpl.DATE_MAX_TIME;
        }
    }
}
