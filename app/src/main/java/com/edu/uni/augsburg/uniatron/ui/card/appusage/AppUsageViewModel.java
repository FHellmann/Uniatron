package com.edu.uni.augsburg.uniatron.ui.card.appusage;

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
import com.edu.uni.augsburg.uniatron.domain.util.DateConverter;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.DateCache;
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
    private final DateCache<Map<String, Long>> mDateCache;
    private final MediatorLiveData<Map<String, Long>> mAppUsages;
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
        final LiveData<Map<String, Long>> data = mRepository.getAppUsageTimeByDate(
                DateConverter.getDateConverterMin(calendarType).convert(date),
                DateConverter.getDateConverterMax(calendarType).convert(date)
        );
        mDateCache.clearAndRegister(mAppUsages, data);
        mAppUsages.addSource(
                data,
                mAppUsages::setValue
        );
    }

    /**
     * Get the app usage card.
     *
     * @param context The context.
     * @return The app usage card.
     */
    @NonNull
    public LiveData<AppUsageCard> getAppUsageCard(@NonNull final Context context) {
        return Transformations.map(mAppUsages, data -> data != null && !data.isEmpty() ? getAppUsageCard(context, data) : null);
    }

    @NonNull
    private AppUsageCard getAppUsageCard(final @NonNull Context context, final Map<String, Long> data) {
        final AppUsageCard card = new AppUsageCard();

        final long usageTimeSum = Stream.of(data).mapToLong(Map.Entry::getValue).sum();
        final ExecutorService service = Executors.newCachedThreadPool();

        final List<AppUsageCard.AppUsageItem> appUsageItems = Stream.of(data)
                .map(entry -> getAppUsageItemAsync(context, usageTimeSum, service, entry))
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

    @NonNull
    private AppUsageCard.AppUsageItem getAppUsageItemAsync(@NonNull final Context context,
                                                           final long usageTimeSum,
                                                           @NonNull final ExecutorService service,
                                                           @NonNull final Map.Entry<String, Long> entry) {
        final AppUsageCard.AppUsageItem item = new AppUsageCard.AppUsageItem();
        service.execute(() -> item.setAppLabel(getApplicationLabel(
                context.getPackageManager(),
                entry.getKey()
        )));
        service.execute(() -> item.setAppIcon(getApplicationIcon(
                context.getPackageManager(),
                context.getResources().getDrawable(android.R.drawable.sym_def_app_icon),
                entry.getKey()
        )));
        item.setApplicationUsage(entry.getValue());
        item.setApplicationUsagePercent(entry.getValue() * 100.0 / usageTimeSum);
        return item;
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
