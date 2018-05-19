package com.edu.uni.augsburg.uniatron.notification.installation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.edu.uni.augsburg.uniatron.notification.NotificationSenderUtil;
import com.edu.uni.augsburg.uniatron.notification.NotificationType;

/**
 * The broadcast receiver which is connected to the app installation monitoring.
 *
 * @author Fabio Hellmann
 */
public class AppInstallationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(intent.getAction())
            || Intent.ACTION_PACKAGE_REPLACED.equalsIgnoreCase(intent.getAction())) {
            // App installation registered -> send notification
            NotificationSenderUtil.send(context, intent, NotificationType.APP_INSTALLATION);
        } else if(intent.hasExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME)) {
            // Add installed app to blacklist
            final String packageName = intent.getStringExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME);
            // TODO Add package name to blacklist in preferences
        } else {
            // Unknown event detected
            throw new IllegalStateException("The following intent content could not be "
                                            + "interpreted: " + intent);
        }
    }
}
