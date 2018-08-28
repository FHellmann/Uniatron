package com.edu.uni.augsburg.uniatron.ui.setting;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.AppPreferences;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.orhanobut.logger.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The {@link SettingViewModel} provides the data for the {@link SettingActivity}.
 *
 * @author Fabio Hellmann
 */
public class SettingViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<InstalledApp>> mInstalledApps;
    private final MutableLiveData<List<InstalledApp>> mObservable = new MutableLiveData<>();
    private final AppPreferences mHandler;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public SettingViewModel(@NonNull final Application application) {
        super(application);

        mHandler = AppContext.getInstance(application).getPreferences();

        mInstalledApps = new MediatorLiveData<>();
        mInstalledApps.addSource(mObservable, mInstalledApps::setValue);

        // the blacklist will be instantly updated upon saving the selection the user made
        mHandler.registerListener(
                SharedPreferencesHandler.PREF_APP_BLACKLIST,
                pref -> mObservable.setValue(getInstalledApps(application, mHandler.getAppsBlacklist()))
        );
        mObservable.setValue(getInstalledApps(application, mHandler.getAppsBlacklist()));
    }

    @Override
    protected void onCleared() {
        mHandler.removeListener(SharedPreferencesHandler.PREF_APP_BLACKLIST);
        super.onCleared();
    }

    /**
     * Get the installed apps.
     *
     * @return The app name/package name pairs.
     */
    @NonNull
    public LiveData<List<InstalledApp>> getInstalledApps() {
        return Transformations.map(mInstalledApps, data -> data == null ? Collections.emptyList() : data);
    }

    private static List<InstalledApp> getInstalledApps(@NonNull final Context context, @NonNull final Set<String> appsBlacklist) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if (installedApplications == null) {
            return Collections.emptyList();
        } else {
            final long start = System.currentTimeMillis();
            // Fetch launchable apps (Performance: ~1200ms)
            final List<InstalledApp> linkedElements = getInstalledAppsData(context, installedApplications);
            Logger.d("Fetch installed apps (count=" + installedApplications.size() + ") needs "
                    + (System.currentTimeMillis() - start) + "ms to filter launchable apps (count=" + linkedElements.size() + ")");
            // Concat with sortBy (Performance: ~100ms)
            return Stream.concat(getSelectedItems(linkedElements, appsBlacklist), getUnselectedItems(linkedElements, appsBlacklist))
                    .collect(Collectors.toList());
        }
    }

    @NonNull
    private static List<InstalledApp> getInstalledAppsData(@NonNull final Context context,
                                                           @NonNull final List<ApplicationInfo> installedApplications) {
        return Stream.of(installedApplications)
                // Is this app?
                .filter(item -> !item.packageName.equals(context.getPackageName()))
                // Is system app?
                .filter(item -> isLaunchableApp(context.getPackageManager(), item.packageName))
                // Convert to an object
                .map(item -> new InstalledApp(item.packageName, context.getPackageManager().getApplicationLabel(item).toString()))
                .collect(Collectors.toList());
    }

    @NonNull
    private static Stream<InstalledApp> getSelectedItems(@NonNull final List<InstalledApp> linkedElements,
                                                         @NonNull final Set<String> appsBlacklist) {
        return Stream.of(linkedElements)
                .filter(item -> appsBlacklist.contains(item.getPackageName()))
                .sortBy(item -> item.getLabel().toLowerCase(Locale.getDefault()));
    }

    @NonNull
    private static Stream<InstalledApp> getUnselectedItems(@NonNull final List<InstalledApp> linkedElements,
                                                           @NonNull final Set<String> appsBlacklist) {
        return Stream.of(linkedElements)
                .filter(item -> !appsBlacklist.contains(item.getPackageName()))
                .sortBy(item -> item.getLabel().toLowerCase(Locale.getDefault()));
    }

    private static boolean isLaunchableApp(@NonNull final PackageManager packageManager, @NonNull final String packageName) {
        // Performance: 1-4 ms
        final Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        return intent != null && intent.resolveActivity(packageManager) != null;
    }
}
