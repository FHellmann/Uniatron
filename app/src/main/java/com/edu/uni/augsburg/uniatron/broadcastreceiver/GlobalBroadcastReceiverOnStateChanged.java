package com.edu.uni.augsburg.uniatron.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.edu.uni.augsburg.uniatron.service.LockApplicationService;
import com.edu.uni.augsburg.uniatron.service.StepCountService;

/**
 * The BroadCastReceiver starts the StepCountService as soon as boot is completed.
 *
 * @author Leon Wöhrl
 */
public class GlobalBroadcastReceiverOnStateChanged extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();
        // on device restart, restart the service
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            startService(context, StepCountService.class);
            startService(context, LockApplicationService.class);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            stopService(context,LockApplicationService.class);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            startService(context,LockApplicationService.class);
        }

    }


    private void stopService(final Context context, final Class<?> serviceClass) {
        final Intent serviceIntent = new Intent(context, serviceClass);
        context.stopService(serviceIntent);
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
