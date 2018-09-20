package com.edu.uni.augsburg.uniatron.ui.card.timeaccount;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.AppPreferences;
import com.edu.uni.augsburg.uniatron.domain.dao.AppUsageDao;
import com.edu.uni.augsburg.uniatron.domain.dao.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.DateCache;

import java.util.Calendar;
import java.util.Date;

/**
 * The model is the connection between the data source
 * and the {@link TimeAccountCard}.
 *
 * @author Fabio Hellmann
 */
public class TimeAccountViewModel extends AndroidViewModel implements CardViewModel {
    private final DateCache<Long> mDateCache;
    private final MediatorLiveData<Long> mRemainingAppUsageTime;
    private final AppUsageDao mAppUsageDao;
    private final AppPreferences mPrefHandler;
    private boolean mIsVisible;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public TimeAccountViewModel(@NonNull final Application application) {
        super(application);

        final AppContext instance = AppContext.getInstance(application);
        mAppUsageDao = instance.getAppUsageDao();
        mPrefHandler = instance.getPreferences();
        mDateCache = new DateCache<>();
        mRemainingAppUsageTime = new MediatorLiveData<>();
    }

    @Override
    public void setup(final Date date, final int calendarType) {
        mIsVisible = DateConverter.getMin(Calendar.DATE).convert(date)
                .equals(DateConverter.getMin(Calendar.DATE).convert(new Date()));
        final LiveData<Long> liveData = mAppUsageDao.getRemainingAppUsageTimeToday(mPrefHandler.getAppsBlacklist());
        mDateCache.clearAndRegister(mRemainingAppUsageTime, liveData);
        mRemainingAppUsageTime.addSource(liveData, mRemainingAppUsageTime::setValue);
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
