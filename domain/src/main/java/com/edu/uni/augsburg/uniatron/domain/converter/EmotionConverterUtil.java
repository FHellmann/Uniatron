package com.edu.uni.augsburg.uniatron.domain.converter;

import android.arch.persistence.room.TypeConverter;

import com.edu.uni.augsburg.uniatron.model.Emotions;

/**
 * A converter for the {@link com.edu.uni.augsburg.uniatron.domain.AppDatabase}
 * and the model classes.
 *
 * @author Fabio Hellmann
 */
public final class EmotionConverterUtil {
    private EmotionConverterUtil() {
    }

    /**
     * Converts a long into a date.
     *
     * @param value The long to convert.
     * @return The date.
     */
    @TypeConverter
    public static Emotions fromRawValue(final Integer value) {
        return value == null ? null : Emotions.values()[value];
    }

    /**
     * Converts a date into a long.
     *
     * @param date The date to convert.
     * @return The long.
     */
    @TypeConverter
    public static Integer fromRealValue(final Emotions date) {
        return date == null ? null : date.ordinal();
    }
}
