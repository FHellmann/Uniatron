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
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link SettingViewModel} provides the data for the {@link SettingFragment}.
 *
 * @author Fabio Hellmann
 */
public class SettingViewModel extends AndroidViewModel {
    private final MediatorLiveData<Map<String, String>> mInstalledApps;
    private final SharedPreferencesHandler mHandler;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public SettingViewModel(@NonNull final Application application) {
        super(application);

        mHandler = new SharedPreferencesHandler(application.getBaseContext());

        final MutableLiveData<Map<String, String>> observable = new MutableLiveData<>();
        observable.setValue(getAllInstalledApps(application));

        mInstalledApps = new MediatorLiveData<>();
        mInstalledApps.addSource(observable, mInstalledApps::setValue);

        mHandler.setOnBlacklistChangeListener((packageName, added) -> {
            observable.postValue(getAllInstalledApps(application));
        });
    }

    /**
     * Update the installed apps.
     *
     * @param context The context.
     */
    public void updateInstalledApps(@NonNull final Context context) {
        final MutableLiveData<Map<String, String>> observable = new MutableLiveData<>();
        observable.setValue(getAllInstalledApps(context));
        mInstalledApps.addSource(observable, mInstalledApps::setValue);
    }

    /**
     * Get the installed apps.
     *
     * @param context The context.
     * @return The app name/package name pairs.
     */
    @NonNull
    public LiveData<Map<String, String>> getInstalledApps(@NonNull final Context context) {
        return Transformations.map(mInstalledApps,
                data -> data == null ? Collections.emptyMap() : data);
    }

    private Map<String, String> getAllInstalledApps(final @NonNull Context context) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);

        if (installedApplications == null) {
            return Collections.emptyMap();
        } else {
            return Stream.of(installedApplications)
                    .filter(item -> !item.packageName.equals(
                            context.getApplicationInfo().packageName
                    ))
                    .sorted((appInfo1, appInfo2) -> {
                        final String appName1 = packageManager.getApplicationLabel(appInfo1)
                                .toString();
                        final String appName2 = packageManager.getApplicationLabel(appInfo2)
                                .toString();

                        if (mHandler.getAppsBlacklist().contains(appName1)
                                && mHandler.getAppsBlacklist().contains(appName2)) {
                            return appName1.compareTo(appName2);
                        } else if (mHandler.getAppsBlacklist().contains(appName1)
                                || mHandler.getAppsBlacklist().contains(appName2)) {
                            // Both or one of them are in the blacklist
                            return -1;
                        }
                        return appName1.compareTo(appName2);
                    })
                    .collect(Collectors.toMap(
                            key -> key.packageName,
                            value -> packageManager.getApplicationLabel(value).toString(),
                            (value1, value2) -> value1,
                            LinkedHashMap::new
                    ));
        }
    }
}
