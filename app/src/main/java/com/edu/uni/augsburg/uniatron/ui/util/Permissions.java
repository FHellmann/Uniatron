package com.edu.uni.augsburg.uniatron.ui.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;

/**
 * A helper class for checking whether a permission is granted
 * or not and if not to request it.
 *
 * @author Fabio Hellmann
 */
public enum Permissions {
    /**
     * The permission to usage access.
     */
    @SuppressLint("InlinedApi")
    USAGE_ACCESS_SETTINGS(
            Manifest.permission.PACKAGE_USAGE_STATS,
            Build.VERSION_CODES.LOLLIPOP,
            context -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            },
            context -> {
                final AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsManager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    // no way to find out. assume we need permission
                    return true;
                }
                try {
                    final PackageManager packageManager = context.getPackageManager();
                    final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                    final int mode = appOpsManager.checkOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            applicationInfo.uid,
                            applicationInfo.packageName);
                    return mode != AppOpsManager.MODE_ALLOWED;
                } catch (PackageManager.NameNotFoundException e) {
                    return true;
                }
            }),
    /**
     * The permission to ignore battery optimization.
     */
    @SuppressLint("InlinedApi")
    IGNORE_BATTERY_OPTIMIZATION_SETTINGS(
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Build.VERSION_CODES.M,
            context -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                }
            },
            context -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }
                final PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                return powerManager == null || !powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            });

    @NonNull
    private final String mPermissionString;
    private final int mApiLevelRequired;
    @NonNull
    private final Consumer<Context> mRequest;
    @NonNull
    private final Function<Context, Boolean> mChecker;

    Permissions(@NonNull final String permissionString,
                final int apiLevelRequired,
                @NonNull final Consumer<Context> request,
                @NonNull final Function<Context, Boolean> checker) {
        mPermissionString = permissionString;
        mApiLevelRequired = apiLevelRequired;
        mRequest = request;
        mChecker = checker;
    }

    /**
     * Get the name of the permission.
     *
     * @return The name of the permission.
     */
    @NonNull
    public String getPermissionName() {
        return mPermissionString;
    }

    /**
     * Check whether or not the api requires to request the permission.
     *
     * @return {@code true} if the current api requires the permission.
     */
    public boolean isRelevantForCurrentApi() {
        return Build.VERSION.SDK_INT >= mApiLevelRequired;
    }

    /**
     * Check whether the permission is already granted or not.
     *
     * @param context The context.
     * @return {@code true} if the permission was not granted
     * yet, {@code false} otherwise.
     */
    public boolean isNotGranted(@NonNull final Context context) {
        return isRelevantForCurrentApi() && mChecker.apply(context);
    }

    /**
     * Requests the permission if it's not already granted.
     *
     * @param context The context.
     */
    public void request(@NonNull final Context context) {
        if (isNotGranted(context)) {
            mRequest.accept(context);
        }
    }
}
