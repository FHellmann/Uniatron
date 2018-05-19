package com.edu.uni.augsburg.uniatron.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.edu.uni.augsburg.uniatron.notification.NotificationSenderUtil;
import com.edu.uni.augsburg.uniatron.notification.NotificationType;
import com.edu.uni.augsburg.uniatron.service.StepCountService;

/**
 * The BroadCastReceiver starts the StepCountService as soon as boot is completed.
 *
 * @author Leon Wöhrl
 */
public class GlobalBroadcastReceiverOnStateChanged extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
            startService(context, StepCountService.class);
        } else if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(intent.getAction())) {
            NotificationSenderUtil.send(context, intent, NotificationType.APP_INSTALLATION);
        }
    }

    private void startService(final Context context, final Class<?> serviceClass) {
        final Intent serviceIntent = new Intent(context, serviceClass);

        // fixes crash on post Android O devices;
        // services cannot be started in background!
        // IllegalStateException: Not allowed to start service Intent
        // { StepCountService }: app is in background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
