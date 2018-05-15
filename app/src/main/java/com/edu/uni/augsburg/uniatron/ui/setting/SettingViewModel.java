package com.edu.uni.augsburg.uniatron.ui.setting;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
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
import java.util.Set;

/**
 * The {@link SettingViewModel} provides the data for the {@link SettingFragment}.
 *
 * @author Fabio Hellmann
 */
public class SettingViewModel extends AndroidViewModel {
    private final MutableLiveData<Set<String>> mInstalledApps;

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public SettingViewModel(@NonNull final Application application) {
        super(application);

        mInstalledApps = new MutableLiveData<>();
    }

    /**
     * Get the installed apps.
     *
     * @param context The context of the app.
     * @return The app-names.
     */
    @NonNull
    public LiveData<Set<String>> getInstalledApps(@NonNull final Context context) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = context.getPackageManager();
        final List<ApplicationInfo> installedApplications = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA);

        if (installedApplications != null) {
            final Set<String> result = Stream.of(installedApplications)
                    .map(item -> packageManager.getApplicationLabel(item).toString())
                    .filter(item -> !item.equals(
                            context.getApplicationInfo().loadLabel(packageManager).toString()
                    ))
                    .collect(Collectors.toSet());

            mInstalledApps.setValue(result);
        }

        return Transformations.map(mInstalledApps,
                data -> data == null ? Collections.emptySet() : data);
    }
}
