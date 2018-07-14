package com.edu.uni.augsburg.uniatron.domain.converter;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
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
    public static Emotions fromRawValue(@Nullable final Integer value) {
        return value == null ? null : Stream.of(Emotions.values())
                .filter(emotion -> emotion.getIndex() == value)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * Converts a emotions into a int.
     *
     * @param emotions The emotions to convert.
     * @return The int.
     */
    @TypeConverter
    public static Integer fromRealValue(@Nullable final Emotions emotions) {
        return emotions == null ? null : emotions.getIndex();
    }
}
