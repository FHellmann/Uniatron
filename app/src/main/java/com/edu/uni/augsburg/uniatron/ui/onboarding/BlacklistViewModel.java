package com.edu.uni.augsburg.uniatron.ui.onboarding;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;

import java.util.Collections;
import java.util.List;

/**
 * The {@link BlacklistViewModel} provides the data for
 * the {@link BlacklistSelectionFragment}.
 *
 * @author Fabio Hellmann
 */
public class BlacklistViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<LaunchableAppDetails>> mInstalledApps;
    private final SharedPreferencesHandler mHandler;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public BlacklistViewModel(@NonNull final Application application) {
        super(application);

        mHandler = MainApplication.getInstance(application).getSharedPreferencesHandler();

        mInstalledApps = new MediatorLiveData<>();
        mInstalledApps.setValue(getInstalledApps(application));
    }

    /**
     * Get the installed apps.
     *
     * @return The app name/package name pairs.
     */
    @NonNull
    public LiveData<List<LaunchableAppDetails>> getInstalledApps() {
        return Transformations.map(mInstalledApps, data -> data == null ? Collections.emptyList() : data);
    }

    private static List<LaunchableAppDetails> getInstalledApps(@NonNull final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        if (installedApplications == null) {
            return Collections.emptyList();
        } else {
            return getInstalledAppsData(context, installedApplications);
        }
    }

    @NonNull
    private static List<LaunchableAppDetails> getInstalledAppsData(@NonNull final Context context,
                                                                   @NonNull final List<ApplicationInfo> installedApplications) {
        return Stream.of(installedApplications)
                // Is this app?
                .filter(item -> !item.packageName.equals(context.getPackageName()))
                // Is system app?
                .filter(item -> isLaunchableApp(context.getPackageManager(), item.packageName))
                // Convert to an object
                .map(item -> new LaunchableAppDetails(
                        item.packageName,
                        context.getPackageManager().getApplicationLabel(item).toString(),
                        context.getPackageManager().getApplicationIcon(item)))
                .collect(Collectors.toList());
    }

    private static boolean isLaunchableApp(@NonNull final PackageManager packageManager, @NonNull final String packageName) {
        // Performance: 1-4 ms
        final Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        return intent != null && intent.resolveActivity(packageManager) != null;
    }

    /**
     * Checks whether or not the app package name is already blacklisted.
     *
     * @param appPackageName The package name.
     * @return {@code true} if the app is already blacklisted,
     * {@code false} otherwise.
     */
    public boolean isAppBlacklisted(@NonNull final String appPackageName) {
        return mHandler.getAppsBlacklist().contains(appPackageName);
    }

    /**
     * Adds or removes the app to/from the blacklist.
     *
     * @param appPackageName The package name.
     */
    public void addAppToBlacklist(@NonNull final String appPackageName) {
        if (mHandler.getAppsBlacklist().contains(appPackageName)) {
            mHandler.removeAppFromBlacklist(appPackageName);
        } else {
            mHandler.addAppToBlacklist(appPackageName);
        }
    }
}
