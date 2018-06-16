package com.edu.uni.augsburg.uniatron.service.handler;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;

import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.builder.PackageAddedNotificationBuilder;
import com.edu.uni.augsburg.uniatron.service.AddBlacklistEntryService;
import com.orhanobut.logger.Logger;

/**
 * The handler detects added packages and notifies the user.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedHandler extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Logger.d("Package action received: " + intent);
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                Logger.d("This was an update from the play store ("
                        + getPackageName(intent) + ")");
                context.startService(new Intent(context, AddBlacklistEntryService.class)
                        .putExtra(Intent.EXTRA_RETURN_RESULT, getPackageName(intent)));
            } else {
                Logger.d("A new package was found (" + getPackageName(intent) + ")");
                postNotification(context, intent);
            }
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            removePackageFromBlacklist(context, intent);
        }
    }

    private void postNotification(final Context context, final Intent intent) {
        final AppNotificationBuilder builder = new PackageAddedNotificationBuilder(
                context,
                getLastInstalledAppLabel(context, intent),
                getPackageName(intent)
        );

        final Notification notification = builder.build();
        final int notificationId = builder.getId();

        NotificationManagerCompat.from(context).notify(notificationId, notification);
    }

    private void removePackageFromBlacklist(final Context context, final Intent intent) {
        final SharedPreferencesHandler handler = new SharedPreferencesHandler(context);
        handler.removeAppFromBlacklist(getPackageName(intent));
    }

    private String getLastInstalledAppLabel(final Context context, final Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final String addedPackageName = getPackageName(intent);
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

    private String getPackageName(final Intent intent) {
        final Uri data = intent.getData();
        if (data == null) {
            // If the intent does not provide the installed package name
            throw new IllegalStateException("The intent need's to contain the added package name");
        } else {
            // The intent provides the installed package name
            return data.getEncodedSchemeSpecificPart();
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
