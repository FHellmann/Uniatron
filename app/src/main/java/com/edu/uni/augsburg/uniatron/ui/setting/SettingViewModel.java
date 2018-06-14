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
import java.util.LinkedList;
import java.util.List;

/**
 * The {@link SettingViewModel} provides the data for the {@link SettingFragment}.
 *
 * @author Fabio Hellmann
 */
public class SettingViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<String>> mInstalledApps;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public SettingViewModel(@NonNull final Application application) {
        super(application);

        final MutableLiveData<List<String>> observable = new MutableLiveData<>();
        observable.setValue(getAllInstalledApps(application.getBaseContext()));

        mInstalledApps = new MediatorLiveData<>();
        mInstalledApps.addSource(observable, mInstalledApps::setValue);

        final SharedPreferencesHandler handler =
                new SharedPreferencesHandler(application.getBaseContext());
        handler.setOnBlacklistChangeListener((packageName, added) -> {
            observable.postValue(getAllInstalledApps(application.getBaseContext()));
        });
    }

    /**
     * Get the installed apps.
     *
     * @return The app-names.
     */
    @NonNull
    public LiveData<List<String>> getInstalledApps() {
        return Transformations.map(mInstalledApps,
                data -> data == null ? Collections.emptyList() : data);
    }

    private List<String> getAllInstalledApps(final @NonNull Context context) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);

        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        final String launcherPackageName = packageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;

        if (installedApplications == null) {
            return Collections.emptyList();
        } else {
            final List<String> appNames =
                    Stream.of(installedApplications)
                            // filter our own app
                            .filter(item -> !item.packageName.equals(
                                    context.getApplicationInfo().packageName))
                            // filter the default launcher
                            .filter(item -> !item.packageName.equals(launcherPackageName))
                            // don't fetch system apps or services
                            .filter(info -> (info.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
                                    | ApplicationInfo.FLAG_SYSTEM)) == 0)
                            .map(item -> packageManager.getApplicationLabel(item).toString())
                            .sorted()
                            .collect(Collectors.toList());


            // grab the blacklist created by user, sort and attach the remaining apps
            final SharedPreferencesHandler handler = new SharedPreferencesHandler(getApplication());
            final List<String> appBlacklistList = Stream.of(handler.getAppsBlacklist())
                    .sorted().toList();

            final LinkedList<String> finalList = new LinkedList<>();

            for (final String name : appBlacklistList) {
                finalList.add(name);
                appNames.remove(name);
            }
            for (final String name : appNames) {
                finalList.add(name);
            }
            return finalList;
        }
    }
}
