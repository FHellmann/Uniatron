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
public class AppUsageCollection implements DataCollection<AppUsageItem> {
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

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    public Stream<AppUsageItem> getEntries() {
        return Stream.of(mData);
    }
}
