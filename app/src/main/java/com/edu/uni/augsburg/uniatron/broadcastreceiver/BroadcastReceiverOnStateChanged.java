package com.edu.uni.augsburg.uniatron.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.edu.uni.augsburg.uniatron.service.StepCountService;

/**
 * The BroadCastReceiver starts the StepCountService as soon as boot is completed.
 *
 * @author Leon Wöhrl
 */
public class BroadcastReceiverOnStateChanged extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        // on device restart, restart the service
        if (action.equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                startService(context);
        }
    }

    private void startService(Context context) {
        Intent i = new Intent(context, StepCountService.class);

        // fixes crash on post Android O devices; services cannot be started in background!
        // IllegalStateException: Not allowed to start service Intent { StepCountService }: app is in background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }
}
