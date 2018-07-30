package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataSource;

import java.util.concurrent.TimeUnit;

/**
 * The {@link OnBoardingViewModel} provides the data for
 * the {@link OnBoardingActivity}.
 *
 * @author Leon Wöhrl
 */
public class OnBoardingViewModel extends AndroidViewModel {

    private static final int STEP_BONUS = 500;
    private final DataSource mDataRepository;
    private final SharedPreferencesHandler mSharedPrefsHandler;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public OnBoardingViewModel(final @NonNull Application application) {
        super(application);

        mDataRepository = MainApplication.getDataSource(application);
        mSharedPrefsHandler = MainApplication.getSharedPreferencesHandler(application);
    }

    /**
     * Add the intro bonus.
     *
     * @param packageName The package name of this app.
     */
    public void addIntroBonusIfAvailable(@NonNull final String packageName) {
        if (mSharedPrefsHandler.isIntroBonusEligible()) {
            mSharedPrefsHandler.setIntroBonusGranted();
            mDataRepository.addAppUsage(packageName, TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES));
            mDataRepository.addStepCount(STEP_BONUS);
        }
    }
}
