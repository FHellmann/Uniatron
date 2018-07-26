package com.edu.uni.augsburg.uniatron.ui.util;

import android.support.annotation.NonNull;

import com.annimon.stream.function.Function;

import java.util.Date;
import java.util.Locale;

/**
 * This is a util class to format dates.
 *
 * @author Fabio Hellmann
 */
public enum DateFormatter {
    /**
     * Formats the date in the format "d. MM yyyy".
     */
    DD_MM_YYYY(date -> String.format(Locale.getDefault(), "%te. %tb %tY", date, date, date)),
    /**
     * Formats the date in the format "MMM yyyy".
     */
    MM_YYYY(date -> String.format(Locale.getDefault(), "%tB %tY", date, date)),
    /**
     * Formats the date in the format "yyyy".
     */
    YYYY(date -> String.format(Locale.getDefault(), "%tY", date));

    @NonNull
    private final Function<Date, String> mFunction;

    DateFormatter(@NonNull final Function<Date, String> function) {
        mFunction = function;
    }

    /**
     * Formats the date.
     *
     * @param date The date to format.
     * @return The formatted date as string.
     */
    public String format(@NonNull final Date date) {
        return mFunction.apply(date);
    }
}
