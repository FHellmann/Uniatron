package com.edu.uni.augsburg.uniatron.notification.builder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.edu.uni.augsburg.uniatron.R;
import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.NotificationChannels;
import com.edu.uni.augsburg.uniatron.service.NotificationReceiverService;

/**
 * This notification will be displayed to the user when a new app is installed.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedNotificationBuilder implements AppNotificationBuilder {
    private final Context mContext;
    private final String packageName;
    private final String appLabel;
    private final int requestId;

    /**
     * Ctr.
     *
     * @param context     The context.
     * @param appLabel    The app name.
     * @param packageName The app package name.
     */
    public PackageAddedNotificationBuilder(@NonNull final Context context,
                                           @NonNull final String packageName,
                                           @NonNull final String appLabel) {
        this.mContext = context;
        this.packageName = packageName;
        this.appLabel = appLabel;
        this.requestId = (int) System.currentTimeMillis();
    }

    @Override
    public Notification build() {
        return new NotificationCompat.Builder(mContext, NotificationChannels.BLACKLIST.name())
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(mContext.getString(R.string.notify_package_added))
                .setContentText(mContext.getString(
                        R.string.notify_package_added_summary,
                        appLabel
                ))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getService(
                        mContext,
                        requestId,
                        new Intent(mContext, NotificationReceiverService.class)
                                .putExtra(Intent.EXTRA_RETURN_RESULT, packageName),
                        0))
                .setAutoCancel(true)
                .setOngoing(false)
                .build();
    }

    @Override
    public int getId() {
        return requestId;
    }
}
