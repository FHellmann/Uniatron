package com.edu.uni.augsburg.uniatron.service.handler;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.AppContext;
import com.edu.uni.augsburg.uniatron.AppPreferences;

/**
 * The model is the connection between the data source
 * and the {@link PackageChangeDetector}.
 *
 * @author Fabio Hellmann
 */
public class PackageChangeModel {

    private final AppPreferences mSharedPreferences;

    PackageChangeModel(@NonNull final Context context) {
        mSharedPreferences = AppContext.getInstance(context).getPreferences();
    }

    /**
     * Remove app package from blacklist.
     *
     * @param packageName The package name of the app.
     */
    public void removeBlacklistEntry(@NonNull final String packageName) {
        mSharedPreferences.removeAppFromBlacklist(packageName);
    }

    /**
     * Get the app label of the package name.
     *
     * @param context     The context.
     * @param packageName The package name.
     * @return The app label.
     */
    public String getAppLabel(@NonNull final Context context,
                              @NonNull final String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(
                            packageName,
                            0
                    )).toString();
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Unable to find the added package '"
                    + packageName + "'", e);
        }
    }
}
