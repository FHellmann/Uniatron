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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The {@link SettingViewModel} provides the data for the {@link SettingFragment}.
 *
 * @author Fabio Hellmann
 */
public class SettingViewModel extends AndroidViewModel {
    private final MediatorLiveData<Map<String, String>> mInstalledApps;

    /**
     * Ctr.
     *
     * @param application
     *         The application.
     */
    public SettingViewModel(@NonNull final Application application) {
        super(application);

        mInstalledApps = new MediatorLiveData<>();
        mInstalledApps.addSource(getInstalledApps(application), mInstalledApps::setValue);
    }

    /**
     * Get the installed apps.
     *
     * @return The app-names.
     */
    @NonNull
    public LiveData<Map<String, String>> getInstalledApps() {
        return Transformations.map(
                mInstalledApps,
                data -> data == null ? Collections.emptyMap() : data
        );
    }

    @NonNull
    private LiveData<Map<String, String>> getInstalledApps(@NonNull final Context context) {
        final MutableLiveData<Map<String, String>> observable = new MutableLiveData<>();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);

        if (installedApplications != null) {
            observable.setValue(Stream.of(installedApplications)
                                      .filter(item -> !item.packageName.equals(
                                              context.getApplicationInfo().packageName
                                      ))
                                      .sortBy(item -> item.packageName)
                                      .collect(Collectors.toMap(
                                              key -> key.packageName,
                                              value -> packageManager.getApplicationLabel(value)
                                                                     .toString()
                                      )));
        }

        return observable;
    }
}
