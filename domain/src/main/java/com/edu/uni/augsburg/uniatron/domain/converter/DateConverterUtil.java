package com.edu.uni.augsburg.uniatron.domain.converter;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;

import java.util.Date;

/**
 * A converter for the {@link AppDatabase}
 * and the model classes.
 *
 * @author Fabio Hellmann
 */
public final class DateConverterUtil {
    private DateConverterUtil() {
    }

    /**
     * Converts a long into a date.
     *
     * @param value The long to convert.
     * @return The date.
     */
    @TypeConverter
    public static Date fromTimestamp(@Nullable final Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Converts a date into a long.
     *
     * @param date The date to convert.
     * @return The long.
     */
    @TypeConverter
    public static Long dateToTimestamp(@Nullable final Date date) {
        return date == null ? null : date.getTime();
    }
}
