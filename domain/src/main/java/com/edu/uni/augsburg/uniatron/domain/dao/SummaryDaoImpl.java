package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.edu.uni.augsburg.uniatron.domain.dao.model.Summary;
import com.edu.uni.augsburg.uniatron.domain.query.SummaryQuery;
import com.edu.uni.augsburg.uniatron.domain.table.SummaryEntity;

import java.util.Date;
import java.util.List;

/**
 * The dao implementation for {@link SummaryDao}.
 *
 * @author Fabio Hellmann
 */
class SummaryDaoImpl implements SummaryDao {

    @NonNull
    private final SummaryQuery mSummaryQuery;

    /**
     * Ctr.
     *
     * @param summaryQuery The query to access the summary table.
     */
    SummaryDaoImpl(@NonNull final SummaryQuery summaryQuery) {
        mSummaryQuery = summaryQuery;
    }

    public LiveData<List<Summary>> getSummaryByDate(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        return getSummary(dateFrom, dateTo, mSummaryQuery::getSummariesByDate);
    }

    public LiveData<List<Summary>> getSummaryByMonth(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        return getSummary(dateFrom, dateTo, mSummaryQuery::getSummariesByMonth);
    }

    public LiveData<List<Summary>> getSummaryByYear(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        return getSummary(dateFrom, dateTo, mSummaryQuery::getSummariesByYear);
    }

    private LiveData<List<Summary>> getSummary(@NonNull final Date dateFrom,
                                               @NonNull final Date dateTo,
                                               @NonNull final BiFunction<Date, Date, LiveData<List<SummaryEntity>>> function) {
        return Transformations.map(function.apply(dateFrom, dateTo), data -> Stream.ofNullable(data).collect(Collectors.toList()));
    }
}
