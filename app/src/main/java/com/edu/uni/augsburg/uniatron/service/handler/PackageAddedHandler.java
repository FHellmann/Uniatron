package com.edu.uni.augsburg.uniatron.service.handler;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;

import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.notification.builder.PackageAddedNotificationBuilder;
import com.orhanobut.logger.Logger;

/**
 * The handler detects added packages and notifies the user.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedHandler extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            postNotification(context, intent);
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            removePackageFromBlacklist(context, intent);
        }
    }

    private void postNotification(final Context context, final Intent intent) {
        Logger.d("A new package was found -> notifying user");


        final PackageAddedNotificationBuilder builder =
                new PackageAddedNotificationBuilder(context, intent);

        final Notification notification = builder.build();
        final int notificationId = builder.getId();

        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    private void removePackageFromBlacklist(final Context context, final Intent intent) {
        final Uri data = intent.getData();
        if (data != null) {
            final SharedPreferencesHandler handler = new SharedPreferencesHandler(context);
            handler.removeAppFromBlacklist(data.getEncodedSchemeSpecificPart());
        }
    }

    /**
     * Get the intent filter for this broadcast receiver.
     *
     * @return The intent filter.
     */
    public static IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);

        intentFilter.addDataScheme("package");
        return intentFilter;
    }
}
