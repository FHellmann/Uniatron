package com.edu.uni.augsburg.uniatron.ui.history;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.model.Summary;

import java.util.Date;
import java.util.List;

/**
 * The {@link HistoryViewModel} provides the data for the {@link HistoryFragment}.
 *
 * @author Fabio Hellmann
 */
public class HistoryViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<Summary>> mObservableDaySummary;
    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public HistoryViewModel(@NonNull final Application application) {
        super(application);

        mRepository = MainApplication.getRepository(application);

        mObservableDaySummary = new MediatorLiveData<>();
    }

    /**
     * Register to listen on changes of the summary for the specified time range.
     *
     * @param dateFrom The date to start searching.
     * @param dateTo   The date to end searching.
     */
    public void registerDateRange(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        mObservableDaySummary.addSource(
                mRepository.getSummary(dateFrom, dateTo),
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
