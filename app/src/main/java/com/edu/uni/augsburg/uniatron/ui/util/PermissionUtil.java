package com.edu.uni.augsburg.uniatron.ui.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkBatteryOptimized(context)) {
            context.startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean checkBatteryOptimized(@NonNull final Context context) {
        final PowerManager powerManager = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        final String packageName = context.getApplicationContext().getPackageName();
        return powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName);
    }
}
