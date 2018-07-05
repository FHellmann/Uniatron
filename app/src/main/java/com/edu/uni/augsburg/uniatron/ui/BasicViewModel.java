package com.edu.uni.augsburg.uniatron.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Function;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * The {@link BasicViewModel} provides the data for
 * the {@link MainActivity}.
 *
 * @author Fabio Hellmann
 */
public class BasicViewModel extends AndroidViewModel {
    private final List<CardViewModel> mCardViewModelList;
    private final MediatorLiveData<Calendar> mDateLoaded;
    private Calendar mMinCalendar;
    private Calendar mCalendar;
    private GroupBy mGroupByStrategy;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public BasicViewModel(@NonNull final Application application) {
        super(application);

        MainApplication.getRepository(application).getMinDate().observeForever(date -> {
            final Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);
            mMinCalendar = calendar;
            notifyDataSetChanged();
        });

        mCardViewModelList = new ArrayList<>();
        mMinCalendar = GregorianCalendar.getInstance();
        mCalendar = GregorianCalendar.getInstance();
        mGroupByStrategy = GroupBy.DATE;
        mDateLoaded = new MediatorLiveData<>();
        mDateLoaded.setValue(mCalendar);
    }

    public void registerCardViewModel(@NonNull final CardViewModel cardViewModel) {
        mCardViewModelList.add(cardViewModel);
    }

    private void notifyDataSetChanged() {
        Stream.of(mCardViewModelList)
                .forEach(cardViewModel ->
                        cardViewModel.setup(mCalendar.getTime(), mGroupByStrategy.mCalendarType));
        mDateLoaded.postValue(mCalendar);
    }

    public void nextData() {
        mCalendar.add(mGroupByStrategy.mCalendarType, 1);
        notifyDataSetChanged();
    }

    public void prevData() {
        mCalendar.add(mGroupByStrategy.mCalendarType, -1);
        notifyDataSetChanged();
    }

    public void setGroupByStrategy(@NonNull final GroupBy groupBy) {
        mGroupByStrategy = groupBy;
        notifyDataSetChanged();
    }

    public GroupBy getGroupByStrategy() {
        return mGroupByStrategy;
    }

    public void setDate(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        if (mGroupByStrategy.mNextAvailable.apply(calendar)) {
            calendar.setTime(new Date());
        } else if (mGroupByStrategy.mPrevAvailable.apply(mMinCalendar, calendar)) {
            calendar.setTime(mMinCalendar.getTime());
        }
        mCalendar = calendar;
        notifyDataSetChanged();
    }

    public int getCurrentDateValue(final int calendarField) {
        return mCalendar.get(calendarField);
    }

    public boolean isNextAvailable() {
        return mGroupByStrategy.mNextAvailable.apply(mCalendar);
    }

    public boolean isPrevAvailable() {
        return mGroupByStrategy.mPrevAvailable.apply(mMinCalendar, mCalendar);
    }

    public LiveData<Calendar> getCurrentDate() {
        return mDateLoaded;
    }

    public enum GroupBy {
        DATE(
                Calendar.DATE,
                calendar -> !DateUtil.getMinTimeOfDate(new Date()).before(calendar.getTime()),
                (calS, calE) -> calE.getTime().after(DateUtil.getMaxTimeOfDate(calS.getTime()))
        ),
        MONTH(
                Calendar.MONTH,
                calendar -> calendar.get(Calendar.YEAR) <= GregorianCalendar.getInstance().get(Calendar.YEAR)
                        && calendar.get(Calendar.MONTH) < GregorianCalendar.getInstance().get(Calendar.MONTH),
                (calS, calE) -> calE.get(Calendar.YEAR) >= calS.get(Calendar.YEAR)
                        && calE.get(Calendar.MONTH) > calS.get(Calendar.MONTH)
        ),
        YEAR(
                Calendar.YEAR,
                calendar -> calendar.get(Calendar.YEAR) < GregorianCalendar.getInstance().get(Calendar.YEAR),
                (calS, calE) -> calE.get(Calendar.YEAR) > calS.get(Calendar.YEAR)
        );

        private final int mCalendarType;
        private final Function<Calendar, Boolean> mNextAvailable;
        private final BiFunction<Calendar, Calendar, Boolean> mPrevAvailable;

        GroupBy(final int calendarType,
                @NonNull final Function<Calendar, Boolean> nextAvailable,
                @NonNull final BiFunction<Calendar, Calendar, Boolean> prevAvailable) {
            mCalendarType = calendarType;
            mNextAvailable = nextAvailable;
            mPrevAvailable = prevAvailable;
        }

        public int getCalendarType() {
            return mCalendarType;
        }
    }
}
