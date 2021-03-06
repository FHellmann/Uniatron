package com.edu.uni.augsburg.uniatron.domain.converter;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;

import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.dao.model.TimeCreditType;

/**
 * A converter for the {@link AppDatabase}
 * and the model classes.
 *
 * @author Fabio Hellmann
 */
public final class TimeCreditTypeConverterUtil {
    private TimeCreditTypeConverterUtil() {
    }

    /**
     * Converts a int into a emotion.
     *
     * @param value The int to convert.
     * @return The emotion.
     */
    @TypeConverter
    public static TimeCreditType fromRawValue(@Nullable final String value) {
        return value == null ? null : TimeCreditType.valueOf(value);
    }

    /**
     * Converts a type into a int.
     *
     * @param type The type to convert.
     * @return The int.
     */
    @TypeConverter
    public static String fromRealValue(@Nullable final TimeCreditType type) {
        return type == null ? null : type.name();
    }
}
