package com.edu.uni.augsburg.uniatron.ui.history;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The {@link HistoryViewModel} provides the data for the HistoryFragment.
 *
 * @author Fabio Hellmann
 */
public class HistoryViewModel extends AndroidViewModel {
    private final List<LiveData<List<Summary>>> mRegisteredLiveDataList;
    private final MediatorLiveData<List<Summary>> mObservableDaySummary;
    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public HistoryViewModel(@NonNull final Application application) {
        super(application);
        mRegisteredLiveDataList = new ArrayList<>();
        mRepository = MainApplication.getRepository(application);
        mObservableDaySummary = new MediatorLiveData<>();
    }

    /**
     * Clear all the registered live datas.
     */
    public void clear() {
        Stream.of(mRegisteredLiveDataList).forEach(mObservableDaySummary::removeSource);
        mRegisteredLiveDataList.clear();
    }

    /**
     * Register to listen on changes of the summary for the specified time range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     */
    public void registerForDateRange(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        final LiveData<List<Summary>> liveData = mRepository.getSummaryByDate(dateFrom, dateTo);
        mRegisteredLiveDataList.add(liveData);
        mObservableDaySummary.addSource(
                liveData,
                mObservableDaySummary::setValue
        );
    }

    /**
     * Register to listen on changes of the summary for the specified time range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     */
    public void registerForMonthRange(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        final LiveData<List<Summary>> liveData = mRepository.getSummaryByMonth(dateFrom, dateTo);
        mRegisteredLiveDataList.add(liveData);
        mObservableDaySummary.addSource(
                liveData,
                mObservableDaySummary::setValue
        );
    }

    /**
     * Register to listen on changes of the summary for the specified time range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     */
    public void registerForYearRange(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        final LiveData<List<Summary>> liveData = mRepository.getSummaryByYear(dateFrom, dateTo);
        mRegisteredLiveDataList.add(liveData);
        mObservableDaySummary.addSource(
                liveData,
                mObservableDaySummary::setValue
        );
    }

    /**
     * Get the summary for the specified time range.
     *
     * @return The summary.
     */
    @NonNull
    public LiveData<List<Summary>> getSummary() {
        return mObservableDaySummary;
    }
}
