package com.edu.uni.augsburg.uniatron.service.tasks;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.edu.uni.augsburg.uniatron.SharedPreferencesHandler;
import com.edu.uni.augsburg.uniatron.notification.NotificationSenderUtil;
import com.edu.uni.augsburg.uniatron.notification.type.PackageAddedNotificationBuilder;
import com.edu.uni.augsburg.uniatron.service.StickyServiceTask;
import com.orhanobut.logger.Logger;

/**
 * The task listens to all new installed apps.
 *
 * @author Fabio Hellmann
 */
public class PackageAddedTask implements StickyServiceTask {

    private final Service mService;
    private final IntentFilter mIntentFilter;
    private final BroadcastReceiver mPackageAddedReciever;

    /**
     * Ctr.
     *
     * @param service The service.
     */
    public PackageAddedTask(@NonNull final Service service) {
        mService = service;
        mIntentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        mPackageAddedReciever = new Receiver(service);
    }

    @Override
    public void onCreate() {
        mService.getApplication().registerReceiver(mPackageAddedReciever, mIntentFilter);
        Logger.d("package added listener was successful started");
    }

    //ensure that we unregister the mReceiver once it's done.
    @Override
    public void onDestroy() {
        mService.getApplication().unregisterReceiver(mPackageAddedReciever);
        Logger.d("package added listener was destroyed");
    }

    /**
     * Creates an intent to call the package added broadcast receiver.
     *
     * @param context The context.
     * @return The intent.
     */
    public static Intent getBroadcastIntent(@NonNull final Context context) {
        return new Intent(context, Receiver.class);
    }

    private static final class Receiver extends BroadcastReceiver {
        private final Service mService;

        /**
         * Ctr.
         *
         * @param service The service.
         */
        Receiver(@NonNull final Service service) {
            super();
            mService = service;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(intent.getAction())) {
                Logger.d("Package added");
                NotificationSenderUtil.setupChannels(context);

                final PackageAddedNotificationBuilder builder =
                        new PackageAddedNotificationBuilder(context, intent);

                final Notification notification = builder.build();
                final int notificationId = builder.getId();

                mService.startForeground(notificationId, notification);
            } else if (intent.hasExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME)) {
                Logger.d("Installed app should be blacklisted");

                final String packageName = intent
                        .getStringExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME);

                final SharedPreferencesHandler preferencesHandler =
                        new SharedPreferencesHandler(context);
                preferencesHandler.addAppToBlacklist(packageName);
            } else {
                Logger.w("Invalid intent: " + intent);
            }
        }
    }
}
