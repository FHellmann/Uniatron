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
     * Converts a int into a emotion.
     *
     * @param value The int to convert.
     * @return The emotion.
     */
    @TypeConverter
    public static Emotions fromRawValue(final Integer value) {
        return value == null ? null : Emotions.values()[value];
    }

    /**
     * Converts a emotions into a int.
     *
     * @param emotions The emotions to convert.
     * @return The int.
     */
    @TypeConverter
    public static Integer fromRealValue(final Emotions emotions) {
        return emotions == null ? null : emotions.ordinal();
    }
}
