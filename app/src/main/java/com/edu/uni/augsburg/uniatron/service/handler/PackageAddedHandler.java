package com.edu.uni.augsburg.uniatron.service.handler;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.notification.NotificationSenderUtil;
import com.edu.uni.augsburg.uniatron.notification.type.PackageAddedNotificationBuilder;
import com.orhanobut.logger.Logger;

/**
 * The handler detects added packages and notifies the user.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedHandler extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(intent.getAction())) {
            if (intent.hasExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME)) {
                addAppToBlacklist(context, intent);
            } else {
                postNotification(context, intent);
            }
        }
    }

    private void postNotification(final Context context, final Intent intent) {
        Logger.d("Package added from user to system");
        NotificationSenderUtil.setupChannels(context);

        final PackageAddedNotificationBuilder builder =
                new PackageAddedNotificationBuilder(context, intent);

        final Notification notification = builder.build();
        final int notificationId = builder.getId();

        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    private void addAppToBlacklist(final Context context, final Intent intent) {
        final String packageName = intent
                .getStringExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME);

        Logger.d("Installed app (" + packageName + ") will be blacklisted");

        final SharedPreferencesHandler preferencesHandler =
                new SharedPreferencesHandler(context);
        preferencesHandler.addAppToBlacklist(packageName);
    }
}
