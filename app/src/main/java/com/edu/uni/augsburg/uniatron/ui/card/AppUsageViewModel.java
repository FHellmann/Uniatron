package com.edu.uni.augsburg.uniatron.ui.card;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link AppUsageCard}.
 *
 * @author Fabio Hellmann
 */
public class AppUsageViewModel extends AndroidViewModel implements CardViewModel {
    private final DateCache<Map<String, Integer>> mDateCache;
    private final MediatorLiveData<Map<String, Integer>> mAppUsages;
    private final DataRepository mRepository;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public AppUsageViewModel(@NonNull final Application application) {
        super(application);
        mDateCache = new DateCache<>();
        mRepository = MainApplication.getRepository(application);
        mAppUsages = new MediatorLiveData<>();
    }

    @Override
    public void setup(@NonNull final Date date, final int calendarType) {
        final LiveData<Map<String, Integer>> data = mRepository.getAppUsageTimeByDate(
                mDateCache.getDateFrom(date, calendarType),
                mDateCache.getDateTo(date, calendarType)
        );
        mDateCache.clearAndRegister(mAppUsages, data);
        mAppUsages.addSource(
                data,
                mAppUsages::setValue
        );
    }

    @NonNull
    public LiveData<AppUsageCard> getAppStatisticsCard(@NonNull final Context context) {
        return Transformations.map(mAppUsages,
                data -> {
                    if (data != null && !data.isEmpty()) {
                        final AppUsageCard card = new AppUsageCard();

                        final int usageTimeSum = Stream.of(data).mapToInt(Map.Entry::getValue).sum();
                        final ExecutorService service = Executors.newCachedThreadPool();

                        final List<AppUsageCard.AppUsageItem> appUsageItems = Stream.of(data)
                                .map(entry -> {
                                    final AppUsageCard.AppUsageItem item = new AppUsageCard.AppUsageItem();

                                    service.execute(() -> {
                                        final String appLabel = getApplicationLabel(
                                                context.getPackageManager(),
                                                entry.getKey()
                                        );
                                        item.setAppLabel(appLabel);
                                    });
                                    service.execute(() -> {
                                        final Drawable appIcon = getApplicationIcon(
                                                context.getPackageManager(),
                                                context.getResources().getDrawable(android.R.drawable.sym_def_app_icon),
                                                entry.getKey()
                                        );
                                        item.setAppIcon(appIcon);
                                    });

                                    item.setApplicationUsage(entry.getValue());
                                    item.setApplicationUsagePercent(entry.getValue() * 100.0 / usageTimeSum);

                                    return item;
                                })
                                .collect(Collectors.toList());

                        service.shutdownNow();
                        try {
                            service.awaitTermination(3, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            Logger.e(e, "");
                        }

                        card.addAll(appUsageItems);
                        return card;
                    }
                    return null;
                });
    }

    @NonNull
    private String getApplicationLabel(@NonNull final PackageManager packageManager,
                                       @NonNull final String packageName) {
        try {
            final ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    @NonNull
    private Drawable getApplicationIcon(@NonNull final PackageManager packageManager,
                                        @NonNull final Drawable placeHolderIcon,
                                        @NonNull final String packageName) {
        try {
            return packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return placeHolderIcon;
        }
    }
}
