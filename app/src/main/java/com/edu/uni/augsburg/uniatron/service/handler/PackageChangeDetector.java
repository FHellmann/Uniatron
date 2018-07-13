package com.edu.uni.augsburg.uniatron.service.handler;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

import com.edu.uni.augsburg.uniatron.notification.AppNotificationBuilder;
import com.edu.uni.augsburg.uniatron.notification.builder.PackageAddedNotificationBuilder;
import com.orhanobut.logger.Logger;

/**
 * The handler detects added packages and notifies the user.
 *
 * @author Fabio Hellmann
 */
public final class PackageChangeDetector extends BroadcastReceiver {
    private final PackageChangeModel mModel;

    private PackageChangeDetector(@NonNull final Context context) {
        super();
        mModel = new PackageChangeModel(context);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                Logger.d("Package updated (added): " + getPackageName(intent));
            } else {
                Logger.d("Package added: " + getPackageName(intent));
                askUserToAddBlacklistEntry(context, getPackageName(intent));
            }
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
                Logger.d("Package updated (removed): " + getPackageName(intent));
            } else {
                Logger.d("Package removed: " + getPackageName(intent));
                mModel.removeBlacklistEntry(getPackageName(intent));
            }
        }
    }

    private void askUserToAddBlacklistEntry(@NonNull final Context context,
                                  @NonNull final String packageName) {
        final AppNotificationBuilder builder = new PackageAddedNotificationBuilder(
                context,
                packageName,
                mModel.getAppLabel(context, packageName)
        );

        final Notification notification = builder.build();
        final int notificationId = builder.getId();

        NotificationManagerCompat.from(context).notify(notificationId, notification);
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
     * Destroys the handler.
     *
     * @param context The context.
     */
    public void destroy(@NonNull final Context context) {
        context.unregisterReceiver(this);
    }

    /**
     * Starts the handler.
     *
     * @param context  The context.
     * @return The handler.
     */
    public static PackageChangeDetector start(@NonNull final Context context) {
        final PackageChangeDetector handler = new PackageChangeDetector(context);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        context.registerReceiver(handler, filter);
        return handler;
    }
}
