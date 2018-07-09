package com.edu.uni.augsburg.uniatron.ui.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.rvalerio.fgchecker.Utils;

/**
 * The util class helps to request permissions.
 *
 * @author Fabio Hellmann
 */
public final class PermissionUtil {
    private PermissionUtil() {
    }

    /**
     * Request the app usage access setting.
     *
     * @param context The context.
     */
    public static void requestUsageAccess(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !Utils.hasUsageStatsPermission(context)) {
            context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    /**
     * Request the ignore batter optimization setting.
     *
     * @param context The context.
     */
    public static void requestIgnoreBatterOptimization(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && needBatteryWhitelistPermission(context)) {
            context.startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        }
    }

    /**
     * Checks if this app is whitelisted in the battery optimization settings.
     *
     * @param context The context.
     * @return true if we need to be added to the battery optimization whitelist.
     */
    public static boolean needBatteryWhitelistPermission(@NonNull final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                return !powerManager
                        .isIgnoringBatteryOptimizations(context.getPackageName());
            }
        }
        return true;
    }

    /**
     * Checks if usage access is needed or has been granted.
     *
     * @param context The context.
     * @return true if access still needs to be granted.
     */
    public static boolean needUsageAccessPermission(@NonNull final Context context) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager;
        AppOpsManager appOpsManager;
        int mode;
        try {
            appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOpsManager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                // no way to find out. assume we need permission
                return true;
            } else {
                packageManager = context.getPackageManager();
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }
            return mode != AppOpsManager.MODE_ALLOWED;

        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }
}
