package com.edu.uni.augsburg.uniatron.domain.dao.model;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.List;

/**
 * The collected app usage for a specified time range.
 *
 * @author Fabio Hellmann
 */
public class StreamCollection<T> implements DataCollection<T> {
    private final List<T> mData;

    /**
     * Ctr.
     *
     * @param dataList The raw data.
     */
    public StreamCollection(@NonNull final List<T> dataList) {
        mData = dataList;
    }

    public boolean isEmpty() {
        return mData.isEmpty();
    }

    public Stream<T> getEntries() {
        return Stream.of(mData);
    }
}
