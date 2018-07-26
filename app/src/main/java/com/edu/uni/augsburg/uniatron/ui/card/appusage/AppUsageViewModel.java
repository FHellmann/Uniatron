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
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.domain.DataRepository;
import com.edu.uni.augsburg.uniatron.domain.util.DateConverter;
import com.edu.uni.augsburg.uniatron.model.AppUsageCollection;
import com.edu.uni.augsburg.uniatron.ui.CardViewModel;
import com.edu.uni.augsburg.uniatron.ui.card.DateCache;

import java.util.Date;

/**
 * The model is the connection between the {@link DataRepository}
 * and the {@link AppUsageCard}.
 *
 * @author Fabio Hellmann
 */
public class AppUsageViewModel extends AndroidViewModel implements CardViewModel {
    private final DateCache<AppUsageCollection> mDateCache;
    private final MediatorLiveData<AppUsageCollection> mAppUsages;
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
        final LiveData<AppUsageCollection> data = mRepository.getAppUsageTimeByDate(
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
        return Transformations.map(mAppUsages, data -> data == null || data.isEmpty() ? null : getAppUsageCard(context, data));
    }

    @NonNull
    private AppUsageCard getAppUsageCard(@NonNull final Context context, @NonNull final AppUsageCollection data) {
        final AppUsageCard card = new AppUsageCard();
        card.addAll(data.getEntries()
                .map(entry -> new AppUsageViewItem(
                        getApplicationLabel(context, entry.getPackageName()),
                        getApplicationIcon(context, entry.getPackageName()),
                        entry.getApplicationUsage(),
                        entry.getApplicationUsagePercent()))
                .collect(Collectors.toList()));
        return card;
    }

    @NonNull
    private String getApplicationLabel(@NonNull final Context context,
                                       @NonNull final String packageName) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            final ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            return packageManager.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    @NonNull
    private Drawable getApplicationIcon(@NonNull final Context context,
                                        @NonNull final String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            return context.getResources().getDrawable(android.R.drawable.sym_def_app_icon);
        }
    }
}
