package com.edu.uni.augsburg.uniatron.ui.setting;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
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
                    mObservable.setValue(getAllInstalledApps());
                });

        mObservable.setValue(getAllInstalledApps());

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

    private Map<String, String> getAllInstalledApps() {
        final Map<String, String> linkedElements = getInstalledAppsData();
        return Stream.concat(
                // Selected apps
                getFilteredItems(linkedElements, mHandler.getAppsBlacklist()::contains),
                // Unselected apps
                getFilteredItems(linkedElements, app -> !mHandler.getAppsBlacklist().contains(app))
        ).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (value1, value2) -> value1,
                LinkedHashMap::new
        ));
    }

    private Stream<Map.Entry<String, String>> getFilteredItems(@NonNull final Map<String, String> linkedElements,
                                                               @NonNull final Function<String, Boolean> filter) {
        return Stream
                .of(linkedElements.entrySet())
                .filter(item -> filter.apply(item.getKey()))
                .sortBy(item -> item.getValue().toLowerCase(Locale.getDefault()));
    }

    private Map<String, String> getInstalledAppsData() {
        final PackageManager packageManager = getApplication().getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);
        return Stream.ofNullable(installedApplications)
                // Is this app?
                .filter(item -> !item.packageName.equals(getApplication().getPackageName()))
                // Is system app?
                .filter(item -> (item.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
                        | ApplicationInfo.FLAG_SYSTEM)) == 0)
                // Is launcher app?
                .filter(item -> isNotLauncherPackage(item.packageName))
                .collect(Collectors.toMap(
                        key -> key.packageName,
                        value -> packageManager.getApplicationLabel(value).toString()
                ));
    }

    private boolean isNotLauncherPackage(@NonNull final String packageName) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final List<ResolveInfo> resolveInfoList = getApplication().getPackageManager().queryIntentActivities(intent, 0);
        return Stream.ofNullable(resolveInfoList)
                .map(item -> item.activityInfo.packageName)
                .noneMatch(item -> item.equals(packageName));
    }
}
