package com.edu.uni.augsburg.uniatron.ui.card;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;

import java.util.Date;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link TimeAccountCard}.
 *
 * @author Fabio Hellmann
 */
public class TimeAccountViewModel extends AndroidViewModel implements CardViewModel {
    private final DateCache<Integer> mDateCache;
    private final MediatorLiveData<Integer> mRemainingAppUsageTime;
    private final DataRepository mRepository;
    private final SharedPreferencesHandler mPrefHandler;
    private boolean mIsVisible;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public TimeAccountViewModel(@NonNull final Application application) {
        super(application);

        mRepository = MainApplication.getRepository(application);
        mPrefHandler = MainApplication.getSharedPreferencesHandler(application);
        mDateCache = new DateCache<>();
        mRemainingAppUsageTime = new MediatorLiveData<>();
    }

    @Override
    public void setup(final Date date, final int calendarType) {
        mIsVisible = DateUtil.isSameDate(date, new Date());
        final LiveData<Integer> liveData = mRepository
                .getRemainingAppUsageTimeToday(mPrefHandler.getAppsBlacklist());
        mDateCache.clearAndRegister(mRemainingAppUsageTime, liveData);
        mRemainingAppUsageTime.addSource(
                liveData,
                mRemainingAppUsageTime::setValue
        );
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The remaining step count.
     */
    @NonNull
    public LiveData<TimeAccountCard> getRemainingAppUsageTime() {
        return Transformations.map(mRemainingAppUsageTime,
                data -> {
                    if (data != null) {
                        final TimeAccountCard timeAccountCard = new TimeAccountCard();
                        timeAccountCard.setTimeLeft(data);
                        timeAccountCard.setVisible(mIsVisible);
                        return timeAccountCard;
                    }
                    return null;
                });
    }
}
