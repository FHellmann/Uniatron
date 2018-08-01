package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Summary;

import java.util.Date;
import java.util.List;

/**
 * The dao to operate on the summary table.
 *
 * @author Fabio Hellmann
 */
public interface SummaryDao {

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    LiveData<List<Summary>> getSummaryByDate(@NonNull Date dateFrom, @NonNull Date dateTo);

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    LiveData<List<Summary>> getSummaryByMonth(@NonNull Date dateFrom, @NonNull Date dateTo);

    /**
     * Get the summaries for a specified date range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     * @return The summaries.
     */
    LiveData<List<Summary>> getSummaryByYear(@NonNull Date dateFrom, @NonNull Date dateTo);

    /**
     * Creates a new instance to access the summary data.
     *
     * @param queryProvider The query provider.
     * @return The summary dao.
     */
    static SummaryDao create(@NonNull final QueryProvider queryProvider) {
        return new SummaryDaoImpl(queryProvider.summaryQuery());
    }
}
