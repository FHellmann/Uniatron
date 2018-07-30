package com.edu.uni.augsburg.uniatron.model;

import com.annimon.stream.Stream;

/**
 * A collection with data of a data source.
 *
 * @param <T> The generic of the data set.
 * @author Fabio Hellmann
 */
public interface DataCollection<T> {

    /**
     * Checks whether or not the collection is empty.
     *
     * @return {@code true} if the collection is empty, {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * Get the entries.
     *
     * @return The entries.
     */
    Stream<T> getEntries();
}
