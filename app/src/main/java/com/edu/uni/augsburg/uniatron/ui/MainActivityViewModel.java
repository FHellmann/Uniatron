package com.edu.uni.augsburg.uniatron.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Function;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.domain.util.DateConverter;
import com.edu.uni.augsburg.uniatron.ui.util.PermissionUtil;

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
    private final SharedPreferencesHandler mSharedPrefsHandler;
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

        mSharedPrefsHandler = MainApplication.getSharedPreferencesHandler(application);
        MainApplication.getRepository(application).getMinDate().observeForever(date -> {
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
                .forEach(cardViewModel ->
                        cardViewModel.setup(mCalendar.getTime(), mGroupByStrategy.mCalendarType));
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
        notifyDataSetChanged();
    }

    /**
     * Get the group by strategy.
     *
     * @return The strategy.
     */
    public GroupBy getGroupByStrategy() {
        return mGroupByStrategy;
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
     * Check whether the intro is needed or not.
     *
     * @param context The context.
     * @return {@code true} if the intro is needed, {@code false} otherwise.
     */
    public boolean isIntroNeeded(@NonNull final Context context) {
        return mSharedPrefsHandler.isFirstStart()
                || PermissionUtil.needBatteryWhitelistPermission(context)
                || PermissionUtil.needUsageAccessPermission(context);
    }

    /**
     * Mark the intro as done.
     */
    public void setIntroDone() {
        mSharedPrefsHandler.setFirstStartDone();
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
                calendar -> !DateConverter.DATE_MIN_TIME.convert(new Date()).before(calendar.getTime()),
                (calS, calE) -> calE.getTime().after(DateConverter.DATE_MAX_TIME.convert(calS.getTime()))
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
