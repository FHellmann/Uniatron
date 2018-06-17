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
import java.util.Locale;
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
     * Get the installed apps.
     *
     * @return The app name/package name pairs.
     */
    @NonNull
    public LiveData<Map<String, String>> getInstalledApps() {
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
            final Map<String, String> linkedElements = Stream.of(installedApplications)
                    .filter(item -> !item.packageName.equals(
                            context.getApplicationInfo().packageName
                    ))
                    .collect(Collectors.toMap(
                            key -> key.packageName,
                            value -> packageManager.getApplicationLabel(value).toString()
                    ));

            final Stream<Map.Entry<String, String>> selectedItems = Stream
                    .of(linkedElements.entrySet())
                    .filter(item -> mHandler.getAppsBlacklist().contains(item.getKey()))
                    .sortBy(item -> item.getValue().toLowerCase(Locale.getDefault()));

            final Stream<Map.Entry<String, String>> unselectedItems = Stream
                    .of(linkedElements.entrySet())
                    .filter(item -> !mHandler.getAppsBlacklist().contains(item.getKey()))
                    .sortBy(item -> item.getValue().toLowerCase(Locale.getDefault()));

            return Stream.concat(selectedItems, unselectedItems)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (value1, value2) -> value1,
                            LinkedHashMap::new
                    ));
        }
    }
}
