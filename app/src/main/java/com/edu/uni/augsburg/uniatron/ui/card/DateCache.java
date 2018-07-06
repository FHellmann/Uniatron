package com.edu.uni.augsburg.uniatron.ui.card;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache to handle the date unregister.
 *
 * @param <T> The generic type of the source live data.
 * @author Fabio Hellmann
 */
public class DateCache<T> {
    private final Map<MediatorLiveData<?>, LiveData<T>> mCache = new HashMap<>();

    /**
     * Unregister all registered live data.
     */
    void unregister() {
        Stream.of(mCache.entrySet()).forEach(entry -> entry.getKey().removeSource(entry.getValue()));
        mCache.clear();
    }

    /**
     * Register a new live data.
     *
     * @param liveData The live data to listen on.
     * @param source   The source live data.
     */
    void register(@NonNull final MediatorLiveData<?> liveData,
                  @NonNull final LiveData<T> source) {
        mCache.put(liveData, source);
    }
}
