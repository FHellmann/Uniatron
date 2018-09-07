package com.edu.uni.augsburg.uniatron.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Function;
import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.domain.dao.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.ui.card.CardViewModel;
import com.edu.uni.augsburg.uniatron.ui.util.DateFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * The {@link MainActivityViewModel} provides the data for
 * the {@link MainActivity}.
 *
 * @author Fabio Hellmann
 */
public class MainActivityViewModel extends AndroidViewModel {
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
    public MainActivityViewModel(@NonNull final Application application) {
        super(application);

        final AppContext instance = AppContext.getInstance(application);
        instance.getAppUsageDao().getMinDate().observeForever(date -> {
            if (date != null) {
                final Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(date);
                mMinCalendar = calendar;
                notifyDataSetChanged();
            }
        });

        mCardViewModelList = new ArrayList<>();
        mMinCalendar = GregorianCalendar.getInstance();
        mCalendar = GregorianCalendar.getInstance();
        mGroupByStrategy = GroupBy.DATE;
        mDateLoaded = new MediatorLiveData<>();
        mDateLoaded.setValue(mCalendar);
    }

    /**
     * Registers a {@link CardViewModel} to be notified on changes.
     *
     * @param cardViewModel The model.
     */
    public void registerCardViewModel(@NonNull final CardViewModel cardViewModel) {
        mCardViewModelList.add(cardViewModel);
        cardViewModel.setup(mCalendar.getTime(), mGroupByStrategy.mCalendarType);
    }

    /**
     * Notifies all registered {@link CardViewModel}s.
     */
    private void notifyDataSetChanged() {
        Stream.of(mCardViewModelList)
                .forEach(cardViewModel -> cardViewModel.setup(mCalendar.getTime(), mGroupByStrategy.mCalendarType));
        mDateLoaded.setValue(mCalendar);
    }

    /**
     * Moves the date focus to the next date.
     */
    public void nextData() {
        mCalendar.add(mGroupByStrategy.mCalendarType, 1);
        notifyDataSetChanged();
    }

    /**
     * Moves the date focus to the previous date.
     */
    public void prevData() {
        mCalendar.add(mGroupByStrategy.mCalendarType, -1);
        notifyDataSetChanged();
    }

    /**
     * Set the date.
     *
     * @param date The date to set.
     */
    public void setDate(@NonNull final Date date) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        if (!mGroupByStrategy.mNextAvailable.apply(calendar)) {
            calendar.setTime(new Date());
        } else if (!mGroupByStrategy.mPrevAvailable.apply(mMinCalendar, calendar)) {
            calendar.setTime(mMinCalendar.getTime());
        }
        mCalendar = calendar;
        notifyDataSetChanged();
    }

    /**
     * Set the group by strategy.
     *
     * @param groupBy The strategy.
     */
    public void setGroupByStrategy(@NonNull final GroupBy groupBy) {
        mGroupByStrategy = groupBy;
        setDate(new Date());
    }

    /**
     * Get the date formatted as defined in the group strategy.
     *
     * @param date The date to format.
     * @return The formatted date.
     */
    public String getDateFormatByGroupStrategy(@NonNull final Date date) {
        switch (mGroupByStrategy) {
            case MONTH:
                return DateFormatter.MM_YYYY.format(date);
            case YEAR:
                return DateFormatter.YYYY.format(date);
            case DATE:
            default:
                return DateFormatter.DD_MM_YYYY.format(date);
        }
    }

    /**
     * Get the calendar field values of the currently selected date.
     *
     * @param calendarField The calendar field.
     * @return The calendar field value.
     */
    public int getCurrentDateValue(final int calendarField) {
        return mCalendar.get(calendarField);
    }

    /**
     * Check whether the next date is available or not.
     *
     * @return {@code true} if the next date is available, {@code false} otherwise.
     */
    public boolean isNextAvailable() {
        return mGroupByStrategy.mNextAvailable.apply(mCalendar);
    }

    /**
     * Check whether the previous date is available or not.
     *
     * @return {@code true} if the previous date is available, {@code false} otherwise.
     */
    public boolean isPrevAvailable() {
        return mGroupByStrategy.mPrevAvailable.apply(mMinCalendar, mCalendar);
    }

    /**
     * Get the current date.
     *
     * @return The current date.
     */
    @NonNull
    public LiveData<Calendar> getCurrentDate() {
        return mDateLoaded;
    }

    /**
     * The group by strategy for the dates.
     *
     * @author Fabio Hellmann
     */
    public enum GroupBy {
        /**
         * Group by date.
         */
        DATE(
                Calendar.DATE,
                calendar -> !DateConverter.getMin(Calendar.DATE).convert(new Date()).before(calendar.getTime()),
                (calS, calE) -> calE.getTime().after(DateConverter.getMax(Calendar.DATE).convert(calS.getTime()))
        ),
        /**
         * Group by month.
         */
        MONTH(
                Calendar.MONTH,
                calendar -> calendar.get(Calendar.YEAR) <= GregorianCalendar.getInstance().get(Calendar.YEAR)
                        && calendar.get(Calendar.MONTH) < GregorianCalendar.getInstance().get(Calendar.MONTH),
                (calS, calE) -> calE.get(Calendar.YEAR) >= calS.get(Calendar.YEAR)
                        && calE.get(Calendar.MONTH) > calS.get(Calendar.MONTH)
        ),
        /**
         * Group by year.
         */
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
    }
}
