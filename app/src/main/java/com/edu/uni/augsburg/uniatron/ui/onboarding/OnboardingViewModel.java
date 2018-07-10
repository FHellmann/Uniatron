package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;

/**
 * The {@link OnboardingViewModel} provides the data for
 * the {@link OnboardingActivity}.
 *
 * @author Leon Wöhrl
 */
public class OnboardingViewModel extends AndroidViewModel {

    private final DataRepository mDataRepository;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public OnboardingViewModel(final @NonNull Application application) {
        super(application);

        mDataRepository = MainApplication.getRepository(application);
    }

    /**
     * Get the DataRepository of the app.
     *
     * @return the data repository of the app
     */
    public DataRepository getDataRepository() {
        return mDataRepository;
    }
}
