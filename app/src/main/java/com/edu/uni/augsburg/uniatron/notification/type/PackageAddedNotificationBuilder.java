package com.edu.uni.augsburg.uniatron.notification.type;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.tasks.PackageAddedTask;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This notification will be displayed to the user when a new app is installed.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedNotificationBuilder implements AppNotificationBuilder {
    private static final int DISPLAY_TIMEOUT_MINUTES = 5;

    private final Context mContext;
    private final Intent mIntent;

    /**
     * Ctr.
     *
     * @param context The context.
     * @param intent  The intent of the event.
     */
    public PackageAddedNotificationBuilder(@NonNull final Context context,
                                           @NonNull final Intent intent) {
        this.mContext = context;
        this.mIntent = intent;
    }

    @Override
    public Notification build() {
        return new NotificationCompat.Builder(mContext, NotificationChannels.BLACKLIST.getName())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(mContext.getString(R.string.notify_package_added))
                .setContentText(mContext.getString(
                        R.string.notify_package_added_summary,
                        getLastInstalledAppLabel()
                ))
                .setTimeoutAfter(TimeUnit.MINUTES.toMinutes(DISPLAY_TIMEOUT_MINUTES))
                .setContentIntent(buildBlacklistAddIntent())
                .setAutoCancel(true)
                .build();
    }

    @Override
    public int getId() {
        return APP_INSTALLATION_ID;
    }

    private PendingIntent buildBlacklistAddIntent() {
        final Intent blacklistIntent = PackageAddedTask.getBroadcastIntent(mContext);
        blacklistIntent.putExtra(
                Intent.EXTRA_INSTALLER_PACKAGE_NAME,
                getLastInstalledAppPackageName()
        );

        return PendingIntent.getBroadcast(
                mContext,
                getId(),
                blacklistIntent,
                0
        );
    }

    private String getLastInstalledAppLabel() {
        final PackageManager packageManager = mContext.getPackageManager();
        try {
            return packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(getLastInstalledAppPackageName(),
                            0)
            ).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

    private String getLastInstalledAppPackageName() {
        final Uri data = mIntent.getData();
        if (data == null) {
            // If the intent does not provide the installed package name
            final PackageManager packageManager = mContext.getPackageManager();
            final List<ApplicationInfo> installedApplications =
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            return Stream.of(installedApplications)
                    .map(applicationInfo -> {
                        try {
                            return packageManager.getPackageInfo(
                                    applicationInfo.packageName,
                                    0
                            );
                        } catch (PackageManager.NameNotFoundException e) {
                            return null;
                        }
                    })
                    .filter(packageInfo -> packageInfo != null)
                    .sortBy(packageInfo -> packageInfo.firstInstallTime)
                    .findFirst()
                    .map(packageInfo -> packageInfo.packageName)
                    .orElse("");
        } else {
            // The intent provides the installed package name
            return data.getEncodedSchemeSpecificPart();
        }
    }
}
