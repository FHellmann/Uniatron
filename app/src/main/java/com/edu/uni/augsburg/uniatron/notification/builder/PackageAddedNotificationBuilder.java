package com.edu.uni.augsburg.uniatron.notification.builder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.AddBlacklistEntryService;

/**
 * This notification will be displayed to the user when a new app is installed.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedNotificationBuilder implements AppNotificationBuilder {
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
        return new NotificationCompat.Builder(mContext, NotificationChannels.BLACKLIST.name())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(mContext.getString(R.string.notify_package_added))
                .setContentText(mContext.getString(
                        R.string.notify_package_added_summary,
                        getLastInstalledAppLabel()
                ))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getService(
                        mContext,
                        0,
                        new Intent(mContext, AddBlacklistEntryService.class)
                                .putExtra(Intent.EXTRA_RETURN_RESULT, getAddedPackageName()),
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .build();
    }

    @Override
    public int getId() {
        return APP_INSTALLATION_ID;
    }

    private String getLastInstalledAppLabel() {
        final PackageManager packageManager = mContext.getPackageManager();
        final String addedPackageName = getAddedPackageName();
        try {
            return packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(
                            addedPackageName,
                            0
                    )).toString();
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Unable to find the added package '"
                    + addedPackageName + "'", e);
        }
    }

    private String getAddedPackageName() {
        final Uri data = mIntent.getData();
        if (data == null) {
            // If the intent does not provide the installed package name
            throw new IllegalStateException("The intent need's to contain the added package name");
        } else {
            // The intent provides the installed package name
            return data.getEncodedSchemeSpecificPart();
        }
    }
}
