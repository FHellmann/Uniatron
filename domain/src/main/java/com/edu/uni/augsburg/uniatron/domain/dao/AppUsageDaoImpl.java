package com.edu.uni.augsburg.uniatron.domain.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.domain.dao.converter.DateConverter;
import com.edu.uni.augsburg.uniatron.domain.dao.model.AppUsage;
import com.edu.uni.augsburg.uniatron.domain.dao.model.DataCollection;
import com.edu.uni.augsburg.uniatron.domain.dao.model.StreamCollection;
import com.edu.uni.augsburg.uniatron.domain.query.AppUsageQuery;
import com.edu.uni.augsburg.uniatron.domain.table.AppUsageEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

/**
 * The dao implementation for {@link AppUsageDao}.
 *
 * @author Fabio Hellmann
 */
class AppUsageDaoImpl implements AppUsageDao {

    private static final int YEAR_1990 = 1990;

    @NonNull
    private final AppUsageQuery mAppUsageQuery;

    /**
     * Ctr.
     *
     * @param appUsageQuery The query to access the app usage table.
     */
    AppUsageDaoImpl(@NonNull final AppUsageQuery appUsageQuery) {
        mAppUsageQuery = appUsageQuery;
    }

    public LiveData<AppUsage> addAppUsage(@NonNull final String packageName, final long usageTime) {
        return LiveDataAsyncTask.execute(() -> {
            final AppUsageEntity appUsageEntity = new AppUsageEntity();
            appUsageEntity.setPackageName(packageName);
            appUsageEntity.setTimestamp(new Date());
            appUsageEntity.setUsageTime(usageTime);
            mAppUsageQuery.add(appUsageEntity);
            return appUsageEntity;
        });
    }

    public LiveData<Date> getMinDate() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(YEAR_1990, 0, 1);
        final Date dateFrom = DateConverter.getMin(Calendar.DATE).convert(calendar.getTime());
        final Date dateTo = DateConverter.getMax(Calendar.DATE).convert(new Date());
        return mAppUsageQuery.getMinDate(dateFrom, dateTo);
    }

    public LiveData<DataCollection<AppUsage>> getAppUsageTimeByDate(@NonNull final Date dateFrom, @NonNull final Date dateTo) {
        return Transformations.map(mAppUsageQuery.loadAppUsageTime(dateFrom, dateTo),
                appUsageList -> new StreamCollection<>(Stream.ofNullable(appUsageList)
                        .peek(item -> item.setUsageTimeAllPercent(item.getUsageTime() * 100.0 / Stream.ofNullable(appUsageList)
                                .mapToLong(AppUsage::getUsageTime)
                                .sum()))
                        .collect(Collectors.toList())));
    }

    public LiveData<Long> getRemainingAppUsageTimeToday(@NonNull final Set<String> filter) {
        final Date dateFrom = DateConverter.getMin(Calendar.DATE).now();
        final Date dateTo = DateConverter.getMax(Calendar.DATE).now();
        return mAppUsageQuery.loadRemainingAppUsageTimeByBlacklist(dateFrom, dateTo, filter);
    }
}
