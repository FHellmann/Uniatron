package com.edu.uni.augsburg.uniatron.model;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Map;

/**
 * The collected app usage for a specified time range.
 *
 * @author Fabio Hellmann
 */
public class AppUsageCollection {
    private final List<AppUsageItem> mData;

    /**
     * Ctr.
     *
     * @param rawData The raw data.
     */
    public AppUsageCollection(@NonNull final Map<String, Long> rawData) {
        final long sum = Stream.of(rawData.values())
                .mapToLong(Long::longValue)
                .sum();
        mData = Stream.of(rawData.entrySet())
                .map(entry -> new AppUsageItem(entry.getKey(), entry.getValue(), entry.getValue() * 100 / sum))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether or not the collection is empty.
     *
     * @return {@code true} if the collection is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    /**
     * Get the entries.
     *
     * @return The entries.
     */
    public Stream<AppUsageItem> getEntries() {
        return Stream.of(mData);
    }
}
