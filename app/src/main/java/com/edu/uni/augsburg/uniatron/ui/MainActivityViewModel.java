package com.edu.uni.augsburg.uniatron.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The {@link MainActivityViewModel} provides the data for
 * the {@link MainActivity}.
 *
 * @author Fabio Hellmann
 */
public class MainActivityViewModel extends AndroidViewModel {
    private final MediatorLiveData<Calendar> mDateLoaded;
    private final MediatorLiveData<Integer> mDataToLoad;
    private final DataRepository mRepository;
    private LiveData<Integer> mSourceToLoad;
    private Calendar mData;
    private int mCalendarData;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public MainActivityViewModel(@NonNull final Application application) {
        super(application);

        mRepository = MainApplication.getRepository(application);

        mData = GregorianCalendar.getInstance();
        mCalendarData = Calendar.DATE;
        mDateLoaded = new MediatorLiveData<>();
        mDateLoaded.setValue(mData);

        mDataToLoad = new MediatorLiveData<>();
        mSourceToLoad = mRepository.getTotalDaysSinceStart();
        mDataToLoad.addSource(
                mSourceToLoad,
                mDataToLoad::setValue
        );
    }

    /**
     * Get the remaining step count for today.
     *
     * @return The remaining step count.
     */
    @NonNull
    public LiveData<Integer> getDataCountToLoad() {
        return Transformations.map(mDataToLoad,
                data -> data != null && data > 0 ? data : 0);
    }

    public void setLoadDayCount() {
        if(mSourceToLoad != null) {
            mDataToLoad.removeSource(mSourceToLoad);
        }
        mCalendarData = Calendar.DATE;
        mSourceToLoad = mRepository.getTotalDaysSinceStart();
        mDataToLoad.addSource(mSourceToLoad, mDataToLoad::setValue);
    }

    public void setLoadMonthCount() {
        if(mSourceToLoad != null) {
            mDataToLoad.removeSource(mSourceToLoad);
        }
        mCalendarData = Calendar.MONTH;
        mSourceToLoad = mRepository.getTotalMonthsSinceStart();
        mDataToLoad.addSource(mSourceToLoad, mDataToLoad::setValue);
    }

    public void setLoadYearCount() {
        if(mSourceToLoad != null) {
            mDataToLoad.removeSource(mSourceToLoad);
        }
        mCalendarData = Calendar.YEAR;
        mSourceToLoad = mRepository.getTotalYearsSinceStart();
        mDataToLoad.addSource(mSourceToLoad, mDataToLoad::setValue);
    }

    public int getCurrentLoadingStrategy() {
        return mCalendarData;
    }

    public void nextData() {
        mData.add(mCalendarData, 1);
        mDateLoaded.postValue(mData);
    }

    public void prevData() {
        mData.add(mCalendarData, -1);
        mDateLoaded.postValue(mData);
    }

    public void setDate(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        mData = calendar;
        mDateLoaded.postValue(mData);
    }

    public int getCurrentDateValue(final int calendarField) {
        return mData.get(calendarField);
    }

    public boolean isToday() {
        return DateUtil.getMinTimeOfDate(new Date()).before(mData.getTime());
    }

    public boolean isLastDate() {
        return false;
    }

    public LiveData<Calendar> getCurrentDate() {
        return mDateLoaded;
    }

    public enum GroupBy {
        DATE, MONTH, YEAR
    }
}
