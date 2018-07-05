package com.edu.uni.augsburg.uniatron.domain.util;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * This is a helper class for date specific actions.
 *
 * @author Fabio Hellmann
 */
public final class DateUtil {

    private static final int HOUR_OF_DAY_MAX = 23;
    private static final int MINUTE_MAX = 59;
    private static final int SECOND_MAX = 59;
    private static final int MILLISECOND_MAX = 999;

    private DateUtil() {
    }

    /**
     * Get the earliest time of the specified date.
     *
     * @param date The date to get the earliest time.
     * @return The date.
     */
    @NonNull
    public static Date getMinTimeOfDate(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Get the latest time of the specified date.
     *
     * @param date The date to get the latest time.
     * @return The date.
     */
    @NonNull
    public static Date getMaxTimeOfDate(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY_MAX);
        calendar.set(Calendar.MINUTE, MINUTE_MAX);
        calendar.set(Calendar.SECOND, SECOND_MAX);
        calendar.set(Calendar.MILLISECOND, MILLISECOND_MAX);
        return calendar.getTime();
    }

    /**
     * Get the earliest date of the specified month.
     *
     * @param date The month to get the earliest date.
     * @return The date.
     */
    @NonNull
    public static Date getMinDateOfMonth(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Get the earliest date of the specified month.
     *
     * @param date The month to get the earliest date.
     * @return The date.
     */
    @NonNull
    public static Date getMaxDateOfMonth(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY_MAX);
        calendar.set(Calendar.MINUTE, MINUTE_MAX);
        calendar.set(Calendar.SECOND, SECOND_MAX);
        calendar.set(Calendar.MILLISECOND, MILLISECOND_MAX);
        return calendar.getTime();
    }

    /**
     * Get the earliest month of the specified year.
     *
     * @param date The year to get the earliest month.
     * @return The date.
     */
    @NonNull
    public static Date getMinMonthOfYear(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Get the earliest month of the specified year.
     *
     * @param date The year to get the earliest month.
     * @return The date.
     */
    @NonNull
    public static Date getMaxMonthOfYear(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY_MAX);
        calendar.set(Calendar.MINUTE, MINUTE_MAX);
        calendar.set(Calendar.SECOND, SECOND_MAX);
        calendar.set(Calendar.MILLISECOND, MILLISECOND_MAX);
        return calendar.getTime();
    }

    /**
     * Format the date to a date specific string.
     *
     * @param date The date to format.
     * @return the formatted date.
     */
    @NonNull
    public static String formatForDate(@NonNull final Date date) {
        return String.format(Locale.getDefault(), "%te. %tb %tY", date, date, date);
    }

    /**
     * Format the date to a month specific string.
     *
     * @param date The date to format.
     * @return the formatted date.
     */
    @NonNull
    public static String formatForMonth(@NonNull final Date date) {
        return String.format(Locale.getDefault(), "%tB %tY", date, date);
    }

    /**
     * Format the date to a year specific string.
     *
     * @param date The date to format.
     * @return the formatted date.
     */
    @NonNull
    public static String formatForYear(@NonNull final Date date) {
        return String.format(Locale.getDefault(), "%tY", date);
    }
}
