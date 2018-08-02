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
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.edu.uni.augsburg.uniatron.MainApplication;
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

    /**
     * Ctr.
     *
     * @param application The application.
     */
    public SettingViewModel(@NonNull final Application application) {
        super(application);

        final SharedPreferencesHandler mHandler = MainApplication.getSharedPreferencesHandler(application);

        mInstalledApps = new MediatorLiveData<>();
        mInstalledApps.addSource(mObservable, mInstalledApps::setValue);

        // the blacklist will be instantly updated upon saving the selection the user made
        mHandler.registerOnPreferenceChangeListener((sharedPreferences, key) -> {
            Logger.d("shared prefs changed");
            queryInstalledAppsAsync(application, mHandler.getAppsBlacklist());
        });
        queryInstalledAppsAsync(application, mHandler.getAppsBlacklist());
    }

    private void queryInstalledAppsAsync(@NonNull final Context context, @NonNull final Set<String> blacklist) {
        new AsyncInstalledAppLoader(blacklist, mObservable::setValue).execute(context);
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

    private static final class AsyncInstalledAppLoader extends AsyncTask<Context, Void, List<InstalledApp>> {
        @NonNull
        private final Set<String> mBlacklist;
        @NonNull
        private final Consumer<List<InstalledApp>> mConsumer;

        AsyncInstalledAppLoader(@NonNull final Set<String> blacklist, @NonNull final Consumer<List<InstalledApp>> consumer) {
            super();
            mBlacklist = blacklist;
            mConsumer = consumer;
        }

        @Override
        protected List<InstalledApp> doInBackground(final Context... contexts) {
            final Context context = contexts[0];
            final PackageManager packageManager = context.getPackageManager();
            final List<ApplicationInfo> installedApplications = packageManager
                    .getInstalledApplications(PackageManager.GET_META_DATA);

            if (installedApplications == null) {
                return Collections.emptyList();
            } else {
                final List<InstalledApp> linkedElements = getInstalledAppsData(context, installedApplications);
                return Stream.concat(getSelectedItems(linkedElements), getUnselectedItems(linkedElements)).collect(Collectors.toList());
            }
        }

        @Override
        protected void onPostExecute(final List<InstalledApp> installedApps) {
            mConsumer.accept(installedApps);
        }

        @NonNull
        private List<InstalledApp> getInstalledAppsData(@NonNull final Context context,
                                                        @NonNull final List<ApplicationInfo> installedApplications) {
            return Stream.of(installedApplications)
                    .map(item -> new InstalledApp(item.packageName, context.getPackageManager().getApplicationLabel(item).toString()))
                    // Is this app?
                    .filter(item -> !item.getPackageName().equals(context.getPackageName()))
                    // Is launcher app?
                    .filter(item -> isNotLauncherPackage(context, item))
                    .collect(Collectors.toList());
        }

        @NonNull
        private Stream<InstalledApp> getSelectedItems(@NonNull final List<InstalledApp> linkedElements) {
            return Stream.of(linkedElements)
                    .filter(item -> mBlacklist.contains(item.getPackageName()))
                    .sortBy(item -> item.getLabel().toLowerCase(Locale.getDefault()));
        }

        @NonNull
        private Stream<InstalledApp> getUnselectedItems(@NonNull final List<InstalledApp> linkedElements) {
            return Stream.of(linkedElements)
                    .filter(item -> !mBlacklist.contains(item.getPackageName()))
                    .sortBy(item -> item.getLabel().toLowerCase(Locale.getDefault()));
        }

        private boolean isNotLauncherPackage(@NonNull final Context context, @NonNull final InstalledApp installedApp) {
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            final List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
            return Stream.ofNullable(resolveInfoList)
                    .map(item -> item.activityInfo.packageName)
                    .noneMatch(item -> item.equals(installedApp.getPackageName()));
        }
    }
}
