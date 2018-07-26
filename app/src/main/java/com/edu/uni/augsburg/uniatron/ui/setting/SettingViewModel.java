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
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.MainApplication;
import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The {@link SettingViewModel} provides the data for the {@link SettingActivity}.
 *
 * @author Fabio Hellmann
 */
public class SettingViewModel extends AndroidViewModel {
    private final MediatorLiveData<Map<String, String>> mInstalledApps;
    private final MutableLiveData<Map<String, String>> mObservable = new MutableLiveData<>();
    private final SharedPreferencesHandler mHandler;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public SettingViewModel(@NonNull final Application application) {
        super(application);

        mHandler = MainApplication.getSharedPreferencesHandler(application);

        // the blacklist will be instantly updated upon saving the selection the user made
        MainApplication.getSharedPreferencesHandler(application)
                .registerOnPreferenceChangeListener((sharedPreferences, key) -> {
                    Log.d(getClass().toString(), "shared prefs changed");
                    mObservable.setValue(getAllInstalledApps(getApplication()));
                });

        mObservable.setValue(getAllInstalledApps(application));

        mInstalledApps = new MediatorLiveData<>();
        mInstalledApps.addSource(mObservable, mInstalledApps::setValue);
    }

    /**
     * Get the installed apps.
     *
     * @return The app name/package name pairs.
     */
    @NonNull
    public LiveData<Map<String, String>> getInstalledApps() {
        return Transformations.map(mInstalledApps,
                data -> data == null ? Collections.emptyMap() : data);
    }

    private Map<String, String> getAllInstalledApps(@NonNull final Context context) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);

        if (installedApplications == null) {
            return Collections.emptyMap();
        } else {
            final Map<String, String> linkedElements = getInstalledApps(context, packageManager, installedApplications);
            return Stream.concat(getSelectedItems(linkedElements), getUnselectedItems(linkedElements))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (value1, value2) -> value1,
                            LinkedHashMap::new
                    ));
        }
    }

    @NonNull
    private Stream<Map.Entry<String, String>> getUnselectedItems(@NonNull final Map<String, String> linkedElements) {
        return Stream
                .of(linkedElements.entrySet())
                .filter(item -> !mHandler.getAppsBlacklist().contains(item.getKey()))
                .sortBy(item -> item.getValue().toLowerCase(Locale.getDefault()));
    }

    @NonNull
    private Stream<Map.Entry<String, String>> getSelectedItems(@NonNull final Map<String, String> linkedElements) {
        return Stream
                .of(linkedElements.entrySet())
                .filter(item -> mHandler.getAppsBlacklist().contains(item.getKey()))
                .sortBy(item -> item.getValue().toLowerCase(Locale.getDefault()));
    }

    @NonNull
    private Map<String, String> getInstalledApps(@NonNull final Context context,
                                                 @NonNull final PackageManager packageManager,
                                                 @NonNull final List<ApplicationInfo> installedApplications) {
        return Stream.of(installedApplications)
                .filter(item -> !item.packageName.equals(
                        context.getApplicationInfo().packageName
                ))
                .filter(item -> (item.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
                        | ApplicationInfo.FLAG_SYSTEM)) == 0)
                .filter(item -> !item.packageName.equals(getDefaultLauncherPackageName()))
                .collect(Collectors.toMap(
                        key -> key.packageName,
                        value -> packageManager.getApplicationLabel(value).toString()
                ));
    }

    @NonNull
    private String getDefaultLauncherPackageName() {
        final PackageManager localPackageManager = getApplication().getPackageManager();
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return localPackageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .activityInfo.packageName;
    }
}
